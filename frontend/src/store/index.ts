import Vue from 'vue'
import Vuex from 'vuex'
import {$Vue} from "@/main";
import {Source} from "@/models/source";
import {Tag} from "@/models/tag";
import {MediaFile} from "@/models/media-file";
import {Setting, Settings} from "@/models/setting";
import {Profile} from "@/models/profile";

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        untidyFiles: new Map<number, MediaFile[]>(),
        profileId: -1,
        sources: new Array<Source>(),
        tags: new Array<Tag>(),
        profiles: new Array<Profile>()
    },
    getters: {},
    mutations: {
        tidyFile(state, payload: MediaFile) {
            const files = state.untidyFiles.get(payload.sourceId!)!;
            files.splice(
                files.findIndex(it => it.id === payload.id),
                1
            );
        }
    },
    actions: {
        async reloadAll({dispatch}): Promise<void> {
            await Promise.all([
                dispatch('reloadTags'),
                dispatch('reloadSources'),
                dispatch('reloadProfiles'),
            ]);
        },
        loadProfileId({state}): Promise<void> {
            return $Vue.$http.get(`/setting/${Settings.CURRENT_PROFILE}`).then(resp => {
                state.profileId = Number((resp.data as Setting).value);
            })
        },
        reloadProfiles({state}): Promise<void> {
            return $Vue.$http.get('/profile/all').then(resp => {
                state.profiles = resp.data;
            });
        },
        reloadTags({state}): Promise<void> {
            return $Vue.$http.get('/tag/all').then(resp => {
                state.tags = resp.data;
            });
        },
        async reloadSources({state, dispatch}): Promise<void> {
            await dispatch('reloadUntidy')
            return $Vue.$http.get('/source/all').then(resp => {
                state.sources = resp.data;
            });
        },
        reloadUntidy({state}): Promise<void> {
            return $Vue.$http.get('/index/untidy').then(resp => {
                const result = new Map<number, MediaFile[]>();
                (resp.data as MediaFile[]).forEach(it => {
                    if (result.has(it.sourceId!)) {
                        result.get(it.sourceId!)!.push(it);
                    } else {
                        result.set(it.sourceId!, [it]);
                    }
                });
                state.untidyFiles = result;
            });
        },
        async switchProfile({state, dispatch}, id: number): Promise<void> {
            $Vue.$emit('open-reloader');
            await $Vue.$http.get(`/profile/switch/${id}`);
            state.profileId = id;
            await dispatch('reloadAll');
            $Vue.$emit('close-reloader');
        }
    },
    modules: {}
});
