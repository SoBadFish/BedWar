这里肯定有人会看不懂事件应该怎么玩
直接作用在游戏中的事件是 event.yml 文件 这个决定着游戏的进程

目前插件内置的事件有

time: 设置时间
break: 破坏床
custom: 自定义
effect: 药水效果
command: 执行指令

其中time事件的value参数格式为

"名称:时间"

可变名称为: 绿宝石，钻石，金锭，铜锭 这些随着item.yml 货币名称变化而变化，配置的时候要注意
不变名称: 复活 (直接修改玩家的复活时间)

破坏床就不过多赘述了没有value参数

custom 自定义事件

----------------------------
其中custom 事件的value参数格式为

"类型:事件ID"

类型分为: while random foreach

事件ID: 为 roomEventList 中的位置 0是第一条 后面的以此类推

while: 循环执行x事件

示例 "while:0": 循环执行x事件

random: 随机执行事件

示例 "random:0-5": 从事件ID 0-5中随机执行一个事件 使用","分隔
示例2 "random:0,2,3,5" 随机执行 0 2 3 5事件

foreach: 顺序执行事件

示例 "foreach:0-5" 从0开始执行事件 一直到 5结束
----------------------------
effect 药水事件

其中effect 事件的value参数格式为

单个效果:
value: "药水id:等级:时间"
value: "药水id:时间"
value: "药水id"
其中时间和等级可不填 默认1级1秒 单位是秒


多个效果:

value: [""药水id:时间]

