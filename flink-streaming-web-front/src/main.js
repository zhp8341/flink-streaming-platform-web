import Vue from 'vue'

import Cookies from 'js-cookie'

import 'normalize.css/normalize.css' // a modern alternative to CSS resets

import Element from 'element-ui'
import './styles/element-variables.scss'

import '@/styles/index.scss' // global css

import App from './App'
import store from './store'
import router from './router'

import i18n from './lang' // internationalization
import './icons' // icon
import './permission' // permission control
import './utils/error-log' // error log

import '@/assets/iconfont/iconfont.css' // 引入iconfont

import * as filters from './filters' // global filters
/** 引入dayjs */
import dayjs from 'dayjs'
Vue.prototype.dayjs = dayjs

/** 引入Vue CodeMirror */
import { CodeMirror, codemirror } from 'vue-codemirror'
import 'codemirror/lib/codemirror.css'
CodeMirror.defineExtension('autoFormatRange', function(from, to) { // autoFormatRange已从Codemirror新版本中删除，在此手动注册
  var cm = this
  var outer = cm.getMode(); var text = cm.getRange(from, to).split('\n')
  var state = CodeMirror.copyState(outer, cm.getTokenAt(from).state)
  var tabSize = cm.getOption('tabSize')
  var out = ''; var lines = 0; var atSol = from.ch == 0
  function newline() {
    out += '\n'
    atSol = true
    ++lines
  }
  for (var i = 0; i < text.length; ++i) {
    var stream = new CodeMirror.StringStream(text[i], tabSize)
    while (!stream.eol()) {
      var inner = CodeMirror.innerMode(outer, state)
      var style = outer.token(stream, state); var cur = stream.current()
      stream.start = stream.pos
      if (!atSol || /\S/.test(cur)) {
        out += cur
        atSol = false
      }
      if (!atSol && inner.mode.newlineAfterToken &&
              inner.mode.newlineAfterToken(style, cur, stream.string.slice(stream.pos) || text[i + 1] || '', inner.state)) { newline() }
    }
    if (!stream.pos && outer.blankLine) outer.blankLine(state)
    if (!atSol) newline()
  }
  cm.operation(function() {
    cm.replaceRange(out, from, to)
    for (var cur = from.line + 1, end = from.line + lines; cur <= end; ++cur) { cm.indentLine(cur, 'smart') }
  })
})
// Applies automatic mode-aware indentation to the specified range
CodeMirror.defineExtension('autoIndentRange', function(from, to) {
  var cmInstance = this
  this.operation(function() {
    for (var i = from.line; i <= to.line; i++) {
      cmInstance.indentLine(i, 'smart')
    }
  })
})
Vue.use(codemirror)

/** 全局element-resize-detector监听DOM */
import ElementResizeDetectorMaker from 'element-resize-detector'
Vue.prototype.$erd = ElementResizeDetectorMaker()

/** 把localStorage挂载到Vue实例中使用浏览器本地存储功能 */
Vue.prototype.setLocalValue = function(name, value) {
  if (window.localStorage) {
    localStorage.setItem(name, value)
  }
}
Vue.prototype.getLocalValue = function(name) {
  if (window.localStorage) {
    const value = localStorage.getItem(name)
    return (value) || ''
  }
  return ''
}

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online ! ! !
 */
// if (process.env.NODE_ENV === 'production') {
//   const { mockXHR } = require('../mock')
//   mockXHR()
// }

Vue.use(Element, {
  size: 'small', // Cookies.get('size') || 'medium', // set element-ui default size
  i18n: (key, value) => i18n.t(key, value)
})

// register global utility filters
Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key])
})

Vue.config.productionTip = false

Vue.prototype.openLoading = function() {
  const loading = this.$loading({ // 声明一个loading对象
    lock: true, // 是否锁屏
    text: '正在加载...', // 加载动画的文字
    spinner: 'el-icon-loading', // 引入的loading图标
    background: 'rgba(0, 0, 0, 0.3)', // 背景颜色
    target: '.sub-main', // 需要遮罩的区域
    body: true,
    customClass: 'mask' // 遮罩层新增类名
  })
  setTimeout(function() { // 设定定时器，超时5S后自动关闭遮罩层，避免请求失败时，遮罩层一直存在的问题
    loading.close() // 关闭遮罩层
  }, 5000)
  return loading
}

new Vue({
  el: '#app',
  router,
  store,
  i18n,
  render: h => h(App)
})


import VueClipboard from 'vue-clipboard2'
Vue.use(VueClipboard)

import uploader from 'vue-simple-uploader'
Vue.use(uploader)


