# rabbitMQ


  MQ全称为Message Queue，即消息队列， RabbitMQ是由erlang语言开发，基于AMQP（Advanced Message Queue 高级消息队列协议）协议实现的消息队列，它是一种应用程序之间的通信方法，消息队列在分布式系统开 发中应用非常广泛.
  
  
# 消息队列应用场景

    1、任务异步处理。将不需要同步处理的并且耗时长的操作由消息队列通知消息接收方进行异步处理。提高了应用程序的响应时间。
    2、应用程序解耦合 MQ相当于一个中介，生产方通过MQ与消费方交互，它将应用程序进行解耦合。

# 六种工作莫斯
    1.Work queues 工作队列
        work queues与入门程序相比，多了一个消费端，多个消费端共同消费同一个队列中的消息。 
        应用场景：
            对于 任务过重或任务较多情况使用工作队列可以提高任务处理的速度。
        测试：
            1、使用入门程序，启动多个消费者。
            2、生产者发送多个消息。
        结果：
            1、一条消息只会被一个消费者接收；
            2、rabbit采用轮询的方式将消息是平均发送给消费者的； 
            3、消费者在处理完某条消息后，才会收到下一条消息。

    2.Publish/Subscribe  发布订阅
        1)、每个消费者监听自己的队列。 
            **(一个队列亦可以被多个消费者监听,消息轮询发给多个消费者[工作队列模式])**
        2)、生产者将消息发给broker，由交换机将消息转发到绑定此交换机的每个队列，
            每个绑定交换机的队列都将接收到消息
        案例：
            用户通知，当用户充值成功或转账完成系统通知用户，通知方式有短信、邮件多种方法 。 

    3.Routing  路由 [详情见图解]
        1)、每个消费者监听自己的队列，并且设置routingkey。 
        2)、一个交换机可以绑定多个队列,每个队列可以设置一个或多个routingkey
        3)、生产者将消息发给交换机 [发送消息时需指定routingkey的值]，由交换机根据routingkey来转发消息到指定的队列。
            

    4.Topice  通配符
        1)、每个消费者监听自己的队列，并且设置带统配符的routingkey。
        2)、一个交换机可以绑定多个队列,每个队列可以设置一个或多个带通配符的routingkey
        3)、生产者将消息发给broker，由交换机根据routingkey的值来匹配队列,匹配时采用通配符方式,匹配成功的将消息转发到指定的队列。
        案例：
            根据用户的通知设置去通知用户，设置接收Email的用户只接收Email，设置接收sms的用户只接收sms，设置两种 通知类型都接收的则两种通知都有效。
        本案例的需求使用Routing工作模式能否实现？ 
            使用Routing模式也可以实现本案例，共设置三个 routingkey，分别是email、sms、all，email队列绑定email和 all，sms队列绑定sms和all，这样就可以实现上边案例的功能，实现过程比topics复杂。
        **Topic模式更多加强大，它可以实现Routing、publish/subscirbe模式的功能。** 
        
    5.Header Header转发器
        header模式与routing不同的地方在于，header模式取消routingkey，使用header中的 key/value（键值对）匹配 队列。
        案例：
            根据用户的通知设置去通知用户，设置接收Email的用户只接收Email，设置接收sms的用户只接收sms，设置两种 通知类型都接收的则两种通知都有效。

    6.PRC 远程过程调用
        RPC即客户端远程调用服务端的方法 ，使用MQ可以实现RPC的异步调用，基于Direct交换机实现，流程如下： 
            1、客户端即是生产者就是消费者，向RPC请求队列发送RPC调用消息，同时监听RPC响应队列。
            2、服务端监听RPC请求队列的消息，收到消息后执行服务端的方法，得到方法返回的结果 
            3、服务端将RPC方法 的结果发送到RPC响应队列
            4、客户端（RPC调用方）监听RPC响应队列，接收到RPC调用结果。
            
# 思考
    1、publish/subscribe与work queues有什么区别。
        区别：
            1）work queues不用定义交换机，而publish/subscribe需要定义交换机。 
            2）publish/subscribe的生产方是面向交换机发送消息，work queues的生产方是面向队列发送消息(底层使用默认 交换机)。
            3）publish/subscribe需要设置队列和交换机的绑定，work queues不需要设置，实质上work queues会将队列绑 定到默认的交换机 。
        相同点：
            所以两者实现的发布/订阅的效果是一样的，多个消费端监听同一个队列不会重复消费消息。 
    2、两者用什么好呢? publish/subscribe还是work queues。
        建议使用 publish/subscribe，发布订阅模式比工作队列模式更强大，并且发布订阅模式可以指定自己专用的交换机。
    
    3、Routing模式和Publish/subscibe有啥区别？
        1).Publish/subscibe模式在绑定交换机时不需要指定routingKey,消息会发送到每个绑定交换机的队列.
        2).routing模式要求队列在绑定交换机时要指定routingkey(即队列的routingkey),发送消息将消息发送到和routingkey的值相等的队列中.
           routing模式更加强大,亦可以实现Publish/subscibe的功能
    
    4、Topics与Routing的区别
        二者的基本原理相同,
            即:生产者将消息发给交换机,交换机根据routingkey将消息转发给与routingkey匹配的队列.
            不同:routingkey的匹配方式:
                routing模式: 相等匹配
                topics模式: 通配符匹配
        符号:
            # :匹配一个或多个词 eg: inform.# 可匹配 inform.sms, inform.email, inform.email.sms
            * :只能匹配一个词  eg: inform.*  可匹配 inform.sms, inform.email
        