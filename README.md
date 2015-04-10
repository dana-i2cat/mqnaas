<img src="http://new.opennaas.org/wp-content/uploads/2013/11/opennaas-orange.png" height=150 />

MQNaaS is the evolution of [OpenNaaS](https://github.com/dana-i2cat/opennaas). It uses concepts forming a new architecture.

Deploy and execute
==================

Deploying and executing MQNaaS is quite simple: 

Requirements
------------
MQNaaS has been successfully tested with:

* OS: Windows 7, GNU Linux or Mac OS X
* [Oracle Java SE 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Apache Karaf](http://karaf.apache.org/) 2.3.5 or higher ([Apache Karaf 3.0.x](http://www.apache.org/dyn/closer.cgi/karaf/3.0) is recommended)

Executing
---------

1. Launch Apacke Karaf in regular mode ([instructions](https://karaf.apache.org/manual/latest/users-guide/start-stop.html) to obtain a shell console).
2. Load MQNaaS Karaf feature Maven repository:

   First of all, MQNaaS version needs to be determined. In this example, version `0.0.1-SNAPSHOT` is used: 

```
feature:repo-add mvn:org.mqnaas/mqnaas/0.0.1-SNAPSHOT/xml/features
```

3. Install MQNaaS Karaf feature:

```
feature:install mqnaas
```

4. [Optional] Check correct installation
It is possible to check correct installation with this command:

```
feature:list --installed | grep mqnaas
```

with this expected output:

```
mqnaas                | 0.0.1-SNAPSHOT   | x         |               |
```

Documentation
=============

Read more about MQNaaS [here](docs/).

Licensing
=========

The core of MQNaaS is licensed under [GNU Lesser General Public License version 3](https://www.gnu.org/licenses/lgpl-3.0.html).

Extensions in this repository are licensed under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).

![LGPLv3](http://www.gnu.org/graphics/lgplv3-147x51.png)
![ALv2](http://www.apache.org/images/feather-small.gif)

Contact
=======

* This is our [community page](http://www.opennaas.org/community/).

* Also you can join our [users mailing list](http://lists.opennaas.org/cgi-bin/mailman/listinfo/users) for more information.

* Or if you are a brave thrill seeker adventurer, join our [developers mailing list](http://lists.opennaas.org/cgi-bin/mailman/listinfo/dev).

<a href="https://twitter.com/@OpenNaaS"><img src="http://opennaas.org/wp-content/plugins/acurax-social-media-widget/images/themes/1/twitter.png" height=50 /></a>
<a href="https://www.facebook.com/OpenNaas"><img src="http://opennaas.org/wp-content/plugins/acurax-social-media-widget/images/themes/1/facebook.png" height=50 /></a>
<a href="https://www.youtube.com/user/OpenNaaS"><img src="http://opennaas.org/wp-content/plugins/acurax-social-media-widget/images/themes/1/youtube.png" height=50 /></a>

Developers
==========

* Isart Canyameres
* Adrián Roselló
* Julio Carlos Barrera
* Georg Mansky-Kummert
