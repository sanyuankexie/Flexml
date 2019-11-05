![](https://jitpack.io/v/LukeXeon/flexbox.svg)
### 适用的业务范围
适用于要求强展示、无动画、轻交互且要求随时上线、局部动态化，同时还要兼顾性能的应用场景，这些页面由于性能问题RN和WebView显得太重，例如首页feed流卡片，一级页面的活动页等
### 特性
* 基于facebook开源的litho，异步计算布局，解放主线程，并且直接使用Drawable进行渲染，与WebView相比有更大的性能优势
* 使用Glide作为图片加载引擎，所有图片均可以从网络异步加载，异步图片的加载不会触发litho的视图树的状态更新
* 实现前后端分离，后端下发布局+数据的json，可集成在数据接口下发，本地自主解析渲染布局
* 单View接入，基本无入侵性，可用于替换现有的任意一个静态展示型的View,并支持曝光埋点、点击埋点、点击时间处理等事件
* 提供完整的开发工具链，包括布局实时预览APP（overview），以及mock工具，可通过扫码链接电脑进行实时预览调试
* 基于flexbox布局模型，并且包含丰富的可配置样式，边框，边框颜色，圆角，图片，文本等
* 强大的表达式解析功能，包括数学运算，for语句，三元表达式，简单的java方法调用，使用表达式时需使用'${}'包围
* 布局自适应，布局使用的单位为pt，以设备屏幕宽度为准，1pt=（设备屏幕宽度像素值/360）像素
* 支持原生View复用
### 支持的样式
Xml布局标签所支持的属性
#### 基本样式
每种布局都支持丰富的基本样式，每种Widget都适用
* background 可以是颜色以#开头或是颜色的名字，或者为url，还可以使用渐变色，调用内置函数“draw:gradient”实现渐变色，最后一个参数是可变参数,如下：
```xml
<?xml version="1.0" encoding="utf-8"?>
<Flex
    width="360"
    height="600"
    borderWidth="20"
    borderRadius="30"
    borderColor="red"
    background="${fn:gradient(t2b,red,blue)}">
</Flex>
```
* borderRadius 圆角弧度
* borderWidth 边框宽度
* borderColor 边框颜色
* alignSelf 支持flexStart,flexEnd,center,baseline,stretch
* height 高
* width 宽
* margin 外边距
* marginBottom 外边距
* marginTop 外边距
* marginLeft 外边距
* marginRight 外边距
* padding 内边距
* paddingTop 内边距
* paddingBottom 内边距
* paddingLeft 内边距
* paddingRight 内边距
* clickUrl 一个url，触发EventListener的回调
* reportClick 点击时上报一个json，此json支持数据绑定
* reportView 曝光时上报一个json，此json支持数据绑定
#### Image
使用Glide作为图片加载引擎，支持异步加载
* url 图片来源，一个url
* blurRadius 高斯模糊的半径，最大为25
* blurSampling 高斯模糊的采样率，默认为1
* borderRadius 有圆角弧度时，会裁剪内部
* scaleType 缩放类型，有center，fltXY，fitCenter，fitStart，fitEnd，centerInside
#### Text
用于显示文本
* text 显示文本
* textAlign 文本对齐，支持center，left，right
* textSize 文本大小
* textStyle 文本风格，支持normal，bold
* maxLines 最大行数
* minLines 最小行数
* textColor 字体颜色
#### Flex
flex风格的布局容器
* flexDirection 支持row，rowReverse，column，columnReverse
* flexWrap 容器是否包住内部，支持noWrap，wrap，wrapReverse
* justifyContent 主轴对齐，支持flexStart，flexEnd，center，spaceBetween，spaceAround
* alignItems 副轴对齐，支持flexStart，flexEnd，center，baseline，stretch
* alignContent 支持flexStart，flexEnd，center，baseline，stretch
#### Scroller
* scrollBarEnable 启用或关闭scrollBar
* orientation 水平或者垂直（vertical，horizontal）
#### Frame
与FrameLayout类似
#### Embedded
可在布局中接入传统View
* type 类名
#### for
逻辑标签，将内部的标签展开成多组，三个字段都是必填的
* name 循环中所使用的迭代变量
* from 开始的下标
* to 结束的下标
```xml
<?xml version="1.0" encoding="utf-8"?>
<Flex
  height="${height}" >
  <for name="index" from="1" to="3">
    <Text
      text="${itemTexts[index]}"
      height="100">
    </Text>
  </for>
</Flex>
```
等价于
```xml
<?xml version="1.0" encoding="utf-8"?>
<Flex
  height="${height}" >
    <Text
      text="${itemTexts[1]}"
      height="100">
    </Text>
    <Text
      text="${itemTexts[2]}"
      height="100">
    </Text>
    <Text
      text="${itemTexts[3]}"
      height="100">
    </Text>
</Flex>
```
#### 辅助函数
可以在表达式中调用
* utils:check(o:Any)可以检擦一个变量是否有效，为空或者大小为0的集合或者为空的字符串都会返回false值
* draw:gradient(o:Orientation,vararg colors: String) 第一个参数为渐变色的方向，有t2b，tr2bl，l2r，br2tl，b2t，r2l，tl2br八种方向可选，第二个参数是可变参，可传入若干个颜色的字符串
### 运行测试用例
* 在手机上安装overview
* 确保手机与电脑在同一网络环境下
* 运行mock模块下test文件夹下的MockTestCase（mac上路径可能会出点问题）
* 用overview扫描控制台出现的二维码，即可开始测试
* Ctrl+S保存后会刷新手机上的布局（开启LiveReload时）
* 您可参照mock模块与overview模块进行快速集成
### 利用jitpack集成
```
//Add it in your root build.gradle at the end of repositories:
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
 
//Add the dependency
    dependencies {
	        implementation 'com.github.LukeXeon.flexbox:core:Tag'
	}
```
### 项目中所使用的开源框架
#### mock
* google zxing 二维码
* google gson json解析
#### core
* apache el 数据绑定
* facebook litho 渲染
#### overview
* squareup retrofit2 网络请求
