Minion is a next-generation design for distributed OpenNMS components. Initially, it will
support SNMP and JMX data collection and in the future other scheduled tasks like polling.
It is highly distributable and extremely configurable using Apache Karaf, Apache Camel and 
Apache ActiveMQ in the default implementation.

Minion can be used by simply adding a feature URL to a Karaf installation and performing
some simple configuration.  Karaf scripts that perform this setup are provided for both
the OpenNMS server side as well as the Minion client side.

Terms
=====

<dl>
  <dt>Dominion</dt> <dd>The coordination/configuration controller, which runs in Karaf.
  Currently the Dominion Controller only runs in the Karaf embedded into OpenNMS.</dd>
  <dt>Minion</dt> <dd>The remote "client" which performs scheduled tasks and reports back
  to a Dominion controller.</dd>
  <dt>ActiveMQ</dt> <dd>Apache's implementation of JMS: the Java Message Service, used
  for communicating between Minions and the Dominion controller.</dd>
  <dt>SMNnepO</dt> <dd>The code name for the codebase implementing the Dominion,
  Minions, and sampler code.  If you see this, we probably just need to update the
  documentation. ;)  It is likely to refer to the Minion, and not the Dominion
  controller.</dd>
</dl>

<a name="requirements"></a>Requirements
============

The Dominion controller (the OpenNMS side) has the same requirements of OpenNMS, since
it runs inside OpenNMS's embedded Karaf.

The Minion should run anywhere Java 7 is available. Current Minion code is not compatible
with Java 8.

Installing from RPMs
====================

There are now standalone RPMs available for running Minions with OpenNMS.

Installing the Minion Client RPM
--------------------------------

On the system that should be doing collection, download the "smnnepo" RPM
from http://yum.opennms.org/branches/pjsm-2.0/common/opennms/ and install
it, like so: <code>rpm -Uvh smnnepo-1.13\*.rpm</code>


Installing the Minion Server Components
---------------------------------------

First, you should be running OpenNMS from the <code>pjsm/2.0</code> branch for now.
RPMs are available at http://yum.opennms.org/branches/pjsm-2.0/common/opennms/

You'll need OpenNMS (at least <code>opennms-core</code> and
<code>opennms-webapp-jetty</code>), as well as the Minion webapp from the
same location (<code>opennms-webapp-smnnepo</code>).  Configure OpenNMS for
monitoring locations like you would normally (see below for details), and
then start it up.

Starting the Minion Server in OpenNMS
-------------------------------------

1. Connect to the OpenNMS karaf console: <code>ssh -p 8101 admin@localhost</code>
   <br />(password is 'admin')
2. Run the configuration script: <code>source http://localhost:8980/smnnepo/opennms-setup.karaf</code>

This will update your local feature repository, and then install the
<code>sample-receiver-activemq</code> feature, to allow for listening
on an internal ActiveMQ server.

To enable RRD collection, you will also need to configure and install the
<code>sample-storage-rrd</code> feature.

Starting the SMNnepO Client
---------------------------

1. Edit /etc/sysconfig/smnnepo, and put in the URL for the root of your
   OpenNMS server, the name of your monitoring location, and (optionally) the
   URL for your ActiveMQ broker.
2. Start the SMNnepO client: <code>sudo /etc/init.d/smnnepo start</code>

<hr>

Installing from Source
======================

1. Build [OpenNMS pjsm/2.0](https://github.com/OpenNMS/opennms/tree/pjsm/2.0)
2. Build [Minion](http://github.com/OpenNMS/smnnepo.git)
3. From the Minion source build, copy <code>sampler-repo-webapp/target/smnnepo.war</code>
   to your OpenNMS <code>jetty-webapps/</code> directory.

Configuring OpenNMS
-------------------

1. Configure a remote monitoring location in OpenNMS by following instructions at
   [OpenNMS: Remote Polling](http://www.opennms.org/wiki/Remote_Polling).

    * Create a polling package in poller-configuration.xml.
    * Create a package in collectd-configuration.xml.
    * Create a location-def in monitoring-locations.xml for the location you're polling.
      Make sure it has both the "collection-package-name" and "polling-package-name"
      attributes.

2. If you didn't already, copy the <code>smnnepo.war</code> file from the source build above to your
   <code>$OPENNMS\_HOME/jetty-webapps</code> directory.
3. Restart OpenNMS.

Configuring the Dominion Server in OpenNMS
----------------------------------------

1. Connect to the OpenNMS karaf console: <code>ssh -p 8101 admin@localhost</code>
   <br />(password is 'admin')
2. Run the configuration script: <code>source http://localhost:8980/smnnepo/opennms-setup.karaf</code>

If the Dominion Server is behind a firewall you have to open the following ports:

 * **8980** The port number for the OpenNMS Webapp. 
   The Minion needs this to download the
   <code>smnnepo-setup.karaf</code> file.
 * **61616** The port for the ActiveMQ JMS. 
   The Minion needs this port to send messages 
   to the  Dominion Controller.
   

Configuring a Minion Client
---------------------------

Ensure that the pc running the <code>Minion Client</code> meets the [requirements](#requirements)

1. Download and unpack [Karaf 2.4](http://karaf.apache.org/index/community/download.html).
2. Run Karaf (<code>bin/karaf</code>)
3. Run the configuration script:
   <code>source http://opennms-root:8980/smnnepo/smnnepo-setup.karaf instance username password opennms-root location-name</code>

    * instance: possible values are +root+, +activemq+, +minion+, +sampler+
	* username: The OpenNMS username allowed to make ReST calls
	* password: The password for the ReST user
	* opennms-root: The root URL of the OpenNMS server, _not_ including the
	  <code>/opennms</code> part of the URL.  For example, if you normally connect to your
	  OpenNMS web UI at <code>http://www.example.com:8980/opennms/</code>, you would use
	  "<code>http://www.example.com:8980/</code>" as the OpenNMS root. **Note: Do not forget the protocol http or https, otherwise the installation will fail!**
	* location-name: The name of the location the Minion is running at.  This should match
	  a location in the <code>monitoring-locations.xml</code> file on your OpenNMS server.

Validating Your Minion
======================

All Minions need validation.  ;)

If everything connected correctly, you should be able to browse to the OpenNMS Minion
console at <code>http://opennms-host:8980/opennms/minion/index.jsp</code>.

Each Minion correctly connected to the Dominion Controller should be listed with 
the configured location and status <code>Running</code>.

In addition each Minion should send samples to the Dominion Server.

Troubleshooting
===============

If the Minion does not send samples, follow these instructions:

Ensure the Minion is setup correctly
------------------------------------

 1. Log in the Karaf Console
 2. Set logging to debug: <code>log:set DEBUG</code>
 3. Watch logs with <code>log:tail</code>

Ensure that there are no errors in the log file. 
The errors:

    Memory Usage for the Broker (1024 mb) is more than the maximum available for the JVM: 455 mb - resetting to 70% of maximum available: 318 mb
    Temporary Store limit is 51200 mb, whilst the temporary data directory: /root/apache-karaf-2.3.5/activemq-data/rackspace/tmp_storage only has 13394 mb of usable space - resetting to maximum available 13394 mb.

can be ignored.

 
Ensure the logs contain something like this:
 
    org.apache.camel.camel-core - 2.13.2 | Received SampleSet with 184 sample(s)

The number of samples should be greater than 0. If not, watch for these urls:
 
    blueprint-sampler-config.xml: Parsing agents from URL: http://opennms-root:8980/opennms/rest/config/agents/location-name/SNMP.xml
    blueprint-sampler-config-snmp.xml: Parsing SNMP XML: http://opennms-root:8980/opennms/rest/config/snmp
    blueprint-sampler-config.xml: parseJaxbXml: http://onms-root:8980/opennms/rest/config/location-name/collection
 
Manually invoke the URLs and see the result in your browser. The result should be a valid non-empty XML result.
 
In addition the result of the <code>SNMP.xml</code> must contain a <code>sysObjectId</code>, e.g.
 
    <entry>
        <key>sysObjectId</key>
         <value>.1.3.6.1.x.x.x.x.x.x</value>
     </entry>
 
If everything is set up correctly, you should see the <code>Received SampleSet with 184 sample(s)</code> message every 5 minutes in the karaf log.
 

Ensure the Dominion is setup correctly
--------------------------------------

If you do not get resource graphs for your remote node, follow these instructions.

 1. Ensure your Minion shows up as <code>running</code> at <code>http://opennms-host:8980/opennms/minion/index.jsp</code> 
 2. Stop OpenNMS
 3. Delete <code>$OPENNMS\_HOME/share/rrd/snmp/\*</code> 
 4. Edit <code>$OPENNMS\_HOME/etc/service-configuration.xml</code> and comment out service <code>Collectd</code>
 5. Ensure that the <code>sample-rrd-storage</code> feature is installed
 
For this you have to login to the Dominion karaf
 
    $ ssh -p 8101 admin@localhost 
    
List all installed features:

    $ features:list | grep -i sample
   
You should get a list similar to this:

    [installed  ] [15.0.0] sample-api                              opennms-sampler-15.0.0     Sample Collection and Storage :: API
    [uninstalled] [15.0.0] minion-controller                       opennms-sampler-15.0.0     Minion :: Controller
    [installed  ] [15.0.0] dominion-controller                     opennms-sampler-15.0.0     Dominion :: Controller
    [uninstalled] [15.0.0] sampler-config                          opennms-sampler-15.0.0     Sample Collection and Storage :: Configuration
    [uninstalled] [15.0.0] sampler-config-snmp                     opennms-sampler-15.0.0     Sample Collection and Storage :: SNMP Configuration
    [uninstalled] [15.0.0] sampler-scheduler                       opennms-sampler-15.0.0     Sample Collection and Storage :: Scheduler
    [uninstalled] [15.0.0] sampler-snmp                            opennms-sampler-15.0.0     Sample Collection and Storage :: SNMP Collector
    [uninstalled] [15.0.0] sample-graphs                           opennms-sampler-15.0.0     Sample Collection and Storage :: Graphing
    [installed  ] [15.0.0] sample-storage-rrd                      opennms-sampler-15.0.0     Sample Collection and Storage :: RRD
    [uninstalled] [15.0.0] sample-storage-newts                    opennms-sampler-15.0.0     Sample Collection and Storage :: Newts
    [uninstalled] [15.0.0] sample-storage-file                     opennms-sampler-15.0.0     Sample Collection and Storage :: File Storage
    [uninstalled] [15.0.0] sample-storage-xml                      opennms-sampler-15.0.0     Sample Collection and Storage :: XML Storage
    [uninstalled] [15.0.0] sample-dispatch-activemq                opennms-sampler-15.0.0     Sample Collection and Storage :: ActiveMQ Dispatcher
    [installed  ] [15.0.0] sample-receiver-activemq                opennms-sampler-15.0.0     Sample Collection and Storage :: ActiveMQ Receiver
    [uninstalled] [15.0.0] sampler-with-file-storage               opennms-sampler-15.0.0     Sample Collection and Storage :: Integration :: Sampler with file storage
    [uninstalled] [15.0.0] sampler-with-xml-storage                opennms-sampler-15.0.0     Sample Collection and Storage :: Integration :: Sampler with XML storage
    [uninstalled] [15.0.0] sampler-with-rrd-storage                opennms-sampler-15.0.0     Sample Collection and Storage :: Integration :: Sampler with RRD storage
    [uninstalled] [15.0.0] sampler-with-activemq-export            opennms-sampler-15.0.0     Sample Collection and Storage :: Integration :: Sampler with ActiveMQ export
    
Ensure that <code>sample-receiver-activemq</code> and <code>sample-storage-rrd</code> is installed.
If it is not, install it manually:

    $ features:install sample-storage-rrd
    
After this you should see rrd files at <code>$OPENNMS\_HOME/share/rrd/snmp</code>.

Error executing command: Unable to add repositories
--------------------------------------

If you encounter an error like this:

    Error executing command: Unable to add repositories:
	    Error resolving artifact org.apache.activemq:activemq-karaf:xml:features:5.10.0: Could not find artifact org.apache.activemq:activemq-karaf:xml:features:5.10.0

You have very likely not set up the `org.ops4j.pax.url.mvn.cfg` config file correctly.

Check the logs of your karaf and look for something like `java.net.MalformedURLException: no protocol: 192.168.0.2:8980/smnnepo/`.
This indicates that you have forgotten to set the protocol of the URL (`http` or `https`).
Please invoke the original installation script with the `http|https` protocol.
