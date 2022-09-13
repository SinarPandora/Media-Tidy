import axios, {AxiosInstance, AxiosStatic} from 'axios';
import _Vue from "vue";
import {$Vue} from "@/main";

export const instance: AxiosInstance = (() => {
    const ins = axios.create({});
    ins.interceptors.request.use((config) => {
        config.withCredentials = true;
        config.baseURL = 'http://localhost:9010';
        config.timeout = 20 * 1000;
        return config
    });

    ins.interceptors.response.use((res) => {
        return res
    }, async (error) => {
        if (error.response.data?.known != null) {
            $Vue.$emit('system-error', error.response.data);
        } else if (error.response.status === 404) {
            $Vue.$emit('system-error', '[未知地址] 方法不存在');
        } else {
            $Vue.$emit('system-error', '[未知错误] 发生未知错误，可尝试重试操作');
        }
        return Promise.reject(error.response.data)
    });
    return ins;
})();

export function AxiosPlugin(Vue: typeof _Vue): void {
    Vue.prototype.$http = instance;
}

declare module 'vue/types/vue' {
    interface Vue {
        $http: AxiosStatic;
    }
}
