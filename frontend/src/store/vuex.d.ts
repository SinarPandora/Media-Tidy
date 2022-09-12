import {Store} from 'vuex'
import {MediaFile} from "@/models/media-file";
import {Source} from "@/models/source";
import {Tag} from "@/models/tag";
import {Profile} from "@/models/profile";

declare module '@vue/runtime-core' {
    // 声明自己的 store state
    interface State {
        untidyFiles: Map<number, MediaFile[]>
        profileId: number
        sources: Source[]
        tags: Tag[]
        profiles: Profile[]
    }

    // 为 `this.$store` 提供类型声明
    // noinspection JSUnusedGlobalSymbols
    interface ComponentCustomProperties {
        $store: Store<State>
    }
}
