import Vue from 'vue'
import App from './App.vue'
import store from './store'
import router from './router'
import vuetify from './plugins/vuetify'
import {AxiosPlugin} from './plugins/axios'
import {DayJSPlugin} from './plugins/dayjs'
import 'animate.css/animate.min.css';
import '@/assets/override.scss';
import AudioPlayer from '@liripeng/vue-audio-player';

Vue.config.productionTip = false;
Vue.use(AxiosPlugin);
Vue.use(DayJSPlugin);
Vue.use(AudioPlayer);

export const $Vue = new Vue({
  store,
  vuetify,
  router,
  render: h => h(App)
}).$mount('#app');
