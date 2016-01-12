
[Flux中文文档介绍](http://androidflux.github.io/)

>Flux是Facebook在14年提出的一种Web前端架构，主要用来处理复杂的UI逻辑的一致性问题
（当时是为了解决Web页面的消息通知问题）。

Flux的框架结构如下图所示：

![](https://github.com/guoxiaoxing/android-open-framework/raw/master/image/flux-arch.png)


Flux有三个主要组成部分：

- Dispatcher：分发View中产生的事件
- Store：维护UI状态的PresentationModel，用来维护一组逻辑相关的UI状态。
- View：负责处理UI逻辑和一些简单的事件分发

这和MVC的Model-View-Controller并不是对应关系，这里的View是Controller-View，负责处理UI逻辑和一些简单的事件分发，而在Android平台中，完美的对应的到Activity(或Fragment)和相应的布局文件(layout.xml)。Store部分也不是Model（业务Model），而是维护UI状态的PresentationModel，用来维护一组逻辑相关的UI状态。Dispatcher不会被直接使用，而是通过通过一个帮助类ActionCreator来封装Dispatcher，并提供便捷的方法来分发View中产生的事件，消息的传递通过Action（Action是一个普通的POJO类）来封装。

当用户点击UI上某个按钮的时候，一个完整的流程是这样的：按钮被点击触发回调方法，在回调方法中调用ActionCreator提供的有语义的的方法，ActionCreator会根据传入参数创建Action并通过Dispatcher发送给Store，所有订阅了这个Action的Store会接收到订阅的Action并消化Action，然后Store会发送UI状态改变的事件给相关的Activity（或Fragment)，Activity在收到状态发生改变的事件之后，开始更新UI（更新UI的过程中会从Store获取所有需要的数据）。

Store的设计是很精巧的（比较类似PresentationModel模式），每一个Store仅仅负责一片逻辑相关的UI区域，用来维护这片UI的状态，比如有一个设置界面，它有有很多设置项，那么可以让它对应一个SettingStore，这个Store仅仅用来维护Setting的状态。Store对外仅仅提供get方法，它的更新通过Dispatcher派发的Action来更新，当有新的Action进来的时候，它会负责处理Action，并转化成UI需要的数据。
