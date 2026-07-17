
import { createApp } from 'vue';
import App from './App.vue';
import ElementPlus, {ElIcon} from 'element-plus';
import 'element-plus/dist/index.css' ; // 导入样式


import { createPinia } from 'pinia'
//导入持久化插件
import {createPersistedState} from'pinia-persistedstate-plugin'
import {Lock, Message, User} from "@element-plus/icons-vue";
const persist = createPersistedState()
const pinia = createPinia()
//pinia使用持久化插件
pinia.use(persist)

console.log('App is starting...');
const app = createApp(App);
app.component(ElIcon.name, ElIcon);
app.component('Mail', Message);
app.component('User', User);
app.component('Lock', Lock);
    app.use(pinia)
    app.use(ElementPlus)
app.mount('#app');
