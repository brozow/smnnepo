package org.opennms.netmgt.sampler.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;
import org.opennms.core.network.IPAddress;
import org.opennms.netmgt.api.sample.PackageAgentList;
import org.opennms.netmgt.api.sample.ServiceAgent;
import org.opennms.netmgt.config.collectd.Filter;
import org.opennms.netmgt.config.collectd.IncludeRange;
import org.opennms.netmgt.config.collectd.Package;
import org.opennms.netmgt.config.collectd.Parameter;
import org.opennms.netmgt.config.collectd.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerTest extends CamelBlueprintTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerTest.class);

    @Override
    public boolean isUseAdviceWith() {
        return false;
    }

    @Override
    public boolean isUseDebugger() {
        return false;
    }

    // The location of our Blueprint XML file to be used for testing
    @Override
    protected String getBlueprintDescriptor() {
        return "file:src/main/resources/OSGI-INF/blueprint/blueprint.xml";
    }

    @Test
    public void testScheduleAgents() throws InterruptedException {
        final Scheduler scheduler = new Scheduler(1);
        final CountDownLatch latch = new CountDownLatch(5);
        scheduler.setDispatcher(new Dispatcher() {
            @Override public void dispatch(final CollectionRequest request) {
                LOG.debug("Received request: {}", request);
                latch.countDown();
            }
        });

        final List<ServiceAgent> agents = getAgents();
        final PackageAgentList agentSchedule = new PackageAgentList(getPackage(), agents);
        scheduler.onAgentSchedule(agentSchedule);
        assertTrue(latch.await(8, TimeUnit.SECONDS));
    }

    @Test
    public void testScheduleAgentsWithCamel() throws Exception {
        final MockEndpoint endpoint = getMockEndpoint("mock:dispatch", false);
        endpoint.expectedMessageCount(5);

        final List<ServiceAgent> agents = getAgents();
        final PackageAgentList agentSchedule = new PackageAgentList(getPackage(), agents);
        sendBody("seda:scheduleAgents", agentSchedule);
        endpoint.await(8, TimeUnit.SECONDS);
        endpoint.assertIsSatisfied();
    }

    protected List<ServiceAgent> getAgents() {
        final List<ServiceAgent> agents = new ArrayList<ServiceAgent>();
        agents.add(new ServiceAgent(new IPAddress("192.168.0.1"), 161, "SNMP"));
        agents.add(new ServiceAgent(new IPAddress("192.168.0.2"), 161, "SNMP"));
        agents.add(new ServiceAgent(new IPAddress("192.168.0.3"), 161, "SNMP"));
        agents.add(new ServiceAgent(new IPAddress("192.168.0.4"), 161, "SNMP"));
        agents.add(new ServiceAgent(new IPAddress("192.168.0.5"), 161, "SNMP"));
        return agents;
    }

    protected Package getPackage() {
        final Package pack = new Package();
        pack.setName("foo");
        pack.setFilter(new Filter("IPADDR != '0.0.0.0'"));
        pack.addIncludeRange(new IncludeRange("1.1.1.1", "254.254.254.254"));

        final Service snmp = new Service();
        snmp.setName("SNMP");
        snmp.setInterval(5 * 1000l);
        snmp.setUserDefined("false");
        snmp.setStatus("on");
        snmp.addParameter(new Parameter("collection", "default"));
        snmp.addParameter(new Parameter("thresholding-enabled", "true"));
        pack.addService(snmp);

        return pack;
    }
}