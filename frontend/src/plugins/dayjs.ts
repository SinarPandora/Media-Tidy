import _Vue from 'vue';
import 'dayjs/locale/zh-cn';
import dayjs, {Dayjs} from 'dayjs';

dayjs.locale("zh-cn");

export function DayJSPlugin(Vue: typeof _Vue): void {
    Vue.prototype.$date = dayjs;
}

declare module 'vue/types/vue' {
    interface Vue {
        $date: Dayjs;
    }
}
