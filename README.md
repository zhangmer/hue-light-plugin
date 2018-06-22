# hue-light plugin for Jenkins CI

Use the awesome [Philips hue lights](https://www.meethue.com) to show the state of your builds.

The following states are implemented:

* building => blue
* success => green
* fatal errors => red
* no fatal errors ("unstable") => yellow


## Configuration

1. Create a new user one the hue bridge (http://developers.meethue.com/gettingstarted.html)
2. Open Global Setting and set the
  * IP address of the hue bridge
  * Authorized username of the hue bridge
3. Create a new job or modify an existing job
  * Add post-build action **Colorize Hue-Light**
  * Set the id of the light you want to control


## License

This plugin has been released under the MIT License. It uses

Copyright (c) 2013 Mathias Nestler

Also included is a copy of the [Jue library](https://github.com/Q42/Jue), licensed under the MIT License too.

## Update  2018年6月22日10:03:57

本次更新目的是为了使插件支持在jenkins中配置多个HueBridge，同时在jenkins的配置中需要作出以下调整：

1、系统管理--系统设置--Hue-Light--IP of the bridge（该内容可以配置多个IP，使用英文的逗号[,]隔开）

2、系统管理--系统设置--Hue-Light--Username（该内容可以配置多个username，使用英文的逗号[,]隔开）

3、调整需要修改的任务（工程的配置），构建后操作中的Colorize Hue-Light配置ID of a light，将原先的lightID修改为bridgeIP&lightID,如：192.168.1.108&2
