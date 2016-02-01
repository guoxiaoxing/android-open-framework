#一 Dispatcher

Dispatcher在整个Flux架构里是唯一的。

在Flux应用中Dispatcher是中心枢纽，管理所有的数据流。它实际上管理的是Store注册的一系列回调接口，本身没有
其他逻辑 —— 它仅仅是用来把Action发送到各个Store的一套简单的机制。每个Store都会把自己注册到这里，并提供自
己的回调方法。当ActionCreator给Dispatcher传递一个Action的时候，应用中所有的Store都会通过回调接口收到通知。

随着App的增长，Dispatcher会变得更加重要，它可以通过调整回调方法的触发次序来管理Store之间的依赖关系。Store
可以声明等待其他Store更新完毕再更新自己。

#二 Store

Store包含应用的状态(state)和逻辑(logic)。它扮演的角色和MVC模式中的Model类似，但是它会管理多个对象的
状态 —— 它不是像ORM-Model一样的单独的数据集。Store负责管理App中一片<strong>区域(Domain)</strong>的状态，而
不是简单的ORM数据集。

比如，Facebook的<a href="https://facebook.com/lookback/edit">LookbackVideoEditor(网页应用)</a>使用一个
TimeStore来跟踪视频回放的位置和状态。同时，使用ImageStore来维护一组图片集合。在TodoMVC示例中，TotoStore
类似的维护一组TodoItems集合。Store的特点是即维护了一组数据集合同时也维护了逻辑区域的状态(A store exhibits 
characteristics of both a collection of models and a singleton model of a logical domain)。

如上，Store会把自己注册在Dispatcher上并提供一个回调接口，回调的参数是Action。在Store实现的回调方法内，会
用一个<code>switch</code>语句根据Action类型来处理Action，并提供合适的Hooks来指向Store的一些内部方法(provide 
the proper hooks into the store's internal methods)。这样就可以通过Dispatcher发送Action来更新Store的内部状态。
当Store更新后，它会广播一个事件声明自己的状态已经改变了，然后View会读取这些变化并更新自己。


#三 View

在Flux的网页应用中，Controller-View是一个比较复杂的概念，它是React框架中提出来的，这种View负责监听Store的
状态并更新界面。而在Android应用中这变得非常简单，Controller-View就是Activity或者Fragment，每个Activity或
Fragment都负责管理App的一块功能，负责监听Store并更新界面。

当View收到来自Store的更新事件时，它先会从Store的getter方法获取数据，然后调用自己的 <code>setStat()</code>
或者<code>foreUpdate</code>方法迫使界面重绘。

通常一个Activity可以对应一个Store，但是当Activity包含几个Fragment，每个Fragment的功能比较独立时，也可以让
每个Fragment分别对应自己的Store。

#四 Actions 

Dispatcher会提供一个方法来分发事件到Store，并包含一些数据，这通常封装成一个Action。Action的创建一般被封装到
一个有语境意义的Helper方法（ActionCreator），它会把Action传给Dispatcher。比如，我们会在Todo-List应用中，改
变某条Todo的文字内容，这时可以在ActionCreator类中创建一个方法叫做 <code>updateText(todoId, newText)</code>，
然后在View的事件处理方法中调用这个方法，这样就可以响应用户事件了。ActionCreator还会给Action添加一个合适的类型，
这样Store就知道如何处理这个Action了，比如在这个例子中，类型可以叫<code>TODO_UPDATE_TEXT</code>。

Action也可能来自其他的地方，比如Server或者缓存，这发生在数据初始化的时候。也有可能发生在服务器返回错误码或者服
务器有数据更新的时候（比如推送消息）。
