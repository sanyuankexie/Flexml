<img src="https://s2.ax1x.com/2019/11/15/MayJWq.png" width="100%">

# Gbox ![](https://img.shields.io/badge/language-kotlin-orange) ![](https://jitpack.io/v/LukeXeon/flexbox.svg) ![](https://img.shields.io/badge/platform-android-brightgreen) ![](https://img.shields.io/badge/doc-%E6%8E%98%E9%87%91-blue) ![](https://img.shields.io/badge/license-Apache%202.0-green) ![](https://img.shields.io/badge/email-imlkluo%40qq.com-green)
<img src="https://s2.ax1x.com/2019/11/13/MGZAxI.png" width=150 align=right>


### 1 适用的业务范围
在线上，对于某些适用于要求强展示、轻交互、高可配场景，RN和WebView显得不够灵活，性能表现也不够好。

使用RN时要占据整个Activity，而且Native和Js的通信损耗不可避，WebView的情况则更加糟糕，还要lock主线程来加载webkit。这在二级、三级页面还好，在首页是绝对不能用这种掉性能的方案的。

并且对于首页feed流卡片、一级页面的活动区块来说，这些页面的逻辑本身就不强，而且往往也只是需要局部动态化，所以综合来看RN和WebView都不是最优选，我们需要**第三条路**。
### 2 特性
Gbox是对**业务**以及**性能**友好的，它为了解决上述应用场景中所存在的问题而出现，在性能上更接近于原生的基础上也能够编写简单的逻辑：
* **耗时操作异步化**。将原本View体系中的measure、layout搬到异步线程中去，解放主线程，这也是Gbox之所以高效的原因之一
* **干掉布局层级**。直接使用轻量级的Drawable进行渲染，与WebView相比有更大的性能优势
* **异步图片加载**。使用轻量级Glide作为图片加载引擎，所有图片均可以从网络加载，并且不会触发额外的布局更新
* **敏捷开发，随时上线，前后端分离**。后端下发布局+数据的json，可集成在数据接口下发，本地自主解析渲染布局
* **单容器View接入，基本无入侵性**。可用于替换现有的任意一个静态展示型的View，并支持曝光埋点、点击埋点、点击时间处理等事件
* **提供完整的开发工具链，布局开发可实时预览**。提供布局**实时预览**APP（overview）和mock工具，可通过扫码连接电脑进行实时预览调试
* **基于广泛使用的flexbox布局模型，包含丰富的可配置样式**。例如边框颜色，圆角，图片，文本等
* **强大的布局内绑定表达式，编写布局内逻辑**。绑定表达式支持数学运算，for语句，三元表达式，简单的java方法调用，使用表达式时需使用`${}`包围
* **屏幕适配**。布局使用的单位为是设备独立的pt，以设备屏幕宽度为基准，将屏幕分成360份，1pt=设备屏幕宽度/360
* **使用kotlin实现**。代码实现非常简洁，很适合阅读学习
* **对旧逻辑友好**。支持原生View嵌入Gbox
### 4 开始使用
* 预览截图：


![](https://s2.ax1x.com/2019/11/12/M3oXtA.png)
* 使用指南：👉[掘金文章：Gbox完全使用指南](https://juejin.im/post/5dbaceb5f265da4cf677b8c5)
### 5 从Jitpack获取
Gbox使用jitpack进行构建，在你的根项目的build.gradle中添加
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
然后在模块中依赖
```
	dependencies {
	        implementation 'com.github.LukeXeon.flexbox:core:latest.release'
	}
```
