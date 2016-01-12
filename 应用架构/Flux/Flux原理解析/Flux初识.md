
[Flux中文文档介绍](http://androidflux.github.io/)

>Flux是Facebook在14年提出的一种Web前端架构，主要用来处理复杂的UI逻辑的一致性问题
（当时是为了解决Web页面的消息通知问题）。

Flux的框架结构如下图所示：

![](https://github.com/guoxiaoxing/android-open-framework/raw/master/image/flux-arch.png)

#一 基本结构

Flux有三个主要组成部分：

- Dispatcher：分发View中产生的事件
- Store：维护UI状态的PresentationModel，用来维护一组逻辑相关的UI状态。
- View：负责处理UI逻辑和一些简单的事件分发

这和MVC的Model-View-Controller并不是对应关系，这里的View是Controller-View，负责处理UI逻辑和一些简单的事件分发，而在Android平台中，完美的对应的到Activity(或Fragment)和相应的布局文件(layout.xml)。Store部分也不是Model（业务Model），而是维护UI状态的PresentationModel，用来维护一组逻辑相关的UI状态。Dispatcher不会被直接使用，而是通过通过一个帮助类ActionCreator来封装Dispatcher，并提供便捷的方法来分发View中产生的事件，消息的传递通过Action（Action是一个普通的POJO类）来封装。

当用户点击UI上某个按钮的时候，一个完整的流程是这样的：按钮被点击触发回调方法，在回调方法中调用ActionCreator提供的有语义的的方法，ActionCreator会根据传入参数创建Action并通过Dispatcher发送给Store，所有订阅了这个Action的Store会接收到订阅的Action并消化Action，然后Store会发送UI状态改变的事件给相关的Activity（或Fragment)，Activity在收到状态发生改变的事件之后，开始更新UI（更新UI的过程中会从Store获取所有需要的数据）。

Store的设计是很精巧的（比较类似PresentationModel模式），每一个Store仅仅负责一片逻辑相关的UI区域，用来维护这片UI的状态，比如有一个设置界面，它有有很多设置项，那么可以让它对应一个SettingStore，这个Store仅仅用来维护Setting的状态。Store对外仅仅提供get方法，它的更新通过Dispatcher派发的Action来更新，当有新的Action进来的时候，它会负责处理Action，并转化成UI需要的数据。


#二 数据与事件传递

**在Flux架构的应用中，数据是朝单一方向流动的**，Dispatcher、Stores和Views都是独立的节点，拥有不同的输入和输出。Action是一个简单的对象，包含新的数据和数据类型两种基本属性。

![](https://github.com/guoxiaoxing/android-open-framework/raw/master/image/flux_data_stream.png)

Views可以在响应用户操作的时候产生新的Action：

![](https://github.com/guoxiaoxing/android-open-framework/raw/master/image/flux_data_stream_new_action.png)

所有的数据都通过Dispatcher这个枢纽中心传递。Action通过ActionCreator的帮助类产生并传递给Dispatcher，Action大部分情况下是在用户和View交互的时候产生。然后Dispatcher会调用Store注册在其(Dispatcher)中的回调方法, 把Action发送到所有注册的Store。在Store的回调方法内，Store可以处理任何和自身状态有关联的Action。Store接着会触发一个 change 事件来告知Controller-View数据层发生变化。Controller-View监听这些事件，在事件处理方法中从Store中读取数据。Controller-View会调用自己的setState()方法渲染UI。

![](https://github.com/guoxiaoxing/android-open-framework/raw/master/image/flux_data_stream_new_action_detail.png)

这种结构很容易让我们像函数式响应编程(functional-reactive-programming)或者更具的说是数据流(data-flow-programming)编程(flow-based-programming)一样透析我们的应用，在应用中所有的数据流都是单向，没有双向绑定。App的状态全部是通过Store来维护的，这样可以允许App的各个部分保持高度的解耦。Store之间有时也会存在依赖，他们会通过一个严格的结构来维护，通过来Dispatcher保证数据的同步更新。

数据的双向绑定会导致层叠更新的问题，比如一个对象导致了另一个对象的更新，另一个对象或许又会导致更多对象的更新。随着App的增长，这种效应导致无法预测App的那些部分会因为用户的操作而发生改变。但是当数据的更新只能走一轮的时候（a single round），整个系统就会变得更加可预测。
