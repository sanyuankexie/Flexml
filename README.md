# Gbox
![](https://img.shields.io/badge/language-kotlin-orange)
![](https://jitpack.io/v/LukeXeon/flexbox.svg)
![](https://img.shields.io/badge/platform-android-brightgreen)
![](https://img.shields.io/badge/doc-%E6%8E%98%E9%87%91-blue)
![](https://img.shields.io/badge/license-Apache%202.0-green)
![](https://img.shields.io/badge/organization-GCTA-blue)
### 1 适用的业务范围
适用于要求强展示、无动画、轻交互且要求随时上线、局部动态化，同时还要兼顾性能的应用场景，这些页面由于性能问题RN和WebView显得太重，例如首页feed流卡片，一级页面的活动页等
### 2 特性
Gbox是对**业务**以及**性能**友好的：
* **耗时操作异步化**。将原本View体系中的measure、layout搬到异步线程中去，解放主线程，这也是Gbox之所以高效的原因之一
* **干掉布局层级**。直接使用轻量级的Drawable进行渲染，与WebView相比有更大的性能优势
* **异步图片加载**。使用轻量级Glide作为图片加载引擎，所有图片均可以从网络加载，并且不会触发额外的布局更新
* **敏捷开发，随时上线，前后端分离**。后端下发布局+数据的json，可集成在数据接口下发，本地自主解析渲染布局
* **单容器View接入，基本无入侵性**。可用于替换现有的任意一个静态展示型的View，并支持曝光埋点、点击埋点、点击时间处理等事件
* **提供完整的开发工具链**。布局开发可实时预览，提供布局实时预览APP（overview）和mock工具，可通过扫码连接电脑进行实时预览调试
* **基于广泛使用的flexbox布局模型，包含丰富的可配置样式**，例如边框颜色，圆角，图片，文本等
* **强大的布局内绑定表达式**。包括数学运算，for语句，三元表达式，简单的java方法调用，使用表达式时需使用`${}`包围
* **屏幕适配**，布局使用的单位为是设备独立的pt，以设备屏幕宽度为基准，将屏幕分成360份，1pt=设备屏幕宽度/360
* **使用kotlin实现**，代码实现非常简洁，很适合阅读学习
* **对旧逻辑友好**，支持原生View嵌入Gbox
### 3 教程
掘金文章：👉[Gbox完全使用指南](https://juejin.im/post/5dbaceb5f265da4cf677b8c5)
### 4 从Jitpack获取
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
