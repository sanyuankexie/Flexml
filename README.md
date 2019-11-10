![](https://jitpack.io/v/LukeXeon/flexbox.svg)
![](https://img.shields.io/badge/license-Apache%202.0-green)
![](https://img.shields.io/badge/language-kotlin-orange)
![](https://img.shields.io/badge/platform-android-brightgreen)
### 1 适用的业务范围
适用于要求强展示、无动画、轻交互且要求随时上线、局部动态化，同时还要兼顾性能的应用场景，这些页面由于性能问题RN和WebView显得太重，例如首页feed流卡片，一级页面的活动页等
### 2 特性
* 基于Litho，异步计算布局，解放主线程，并且直接轻量级的Drawable进行渲染，消除布局层级，与WebView相比有更大的性能优势
* 使用Glide作为图片加载引擎，所有图片均可以从网络异步加载，异步图片的加载不会触发litho的视图树的状态更新
* 前后端分离，后端下发布局+数据的json，可集成在数据接口下发，本地自主解析渲染布局
* 单View接入，基本无入侵性，可用于替换现有的任意一个静态展示型的View,并支持曝光埋点、点击埋点、点击时间处理等事件
* 提供完整的开发工具链，包括布局实时预览APP（overview），以及mock工具，可通过扫码链接电脑进行实时预览调试
* 基于Flexbox布局模型，并且包含丰富的可配置样式，边框，边框颜色，圆角，图片，文本等
* 强大的表达式解析功能，包括数学运算，for语句，三元表达式，简单的java方法调用，使用表达式时需使用'${}'包围，虽然不如RN适用面广，但用来展示页面已经完全足够
* 布局自适应，布局使用的单位为pt，以设备屏幕宽度为准，1pt=设备屏幕宽度/360
* 支持原生View复用
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
