<template>
  <v-dialog
      v-model="openDialog"
      width="500px"
      persistent
  >
    <v-card tile outlined class="border--warma-dark bold-border" :loading="isLoading">
      <v-card-title class="warma-yellow text--warma-dark">
        选择配置
      </v-card-title>

      <v-container fluid class="pa-3">
        <v-form ref="form" v-if="openDialog">
          <v-row>
            <v-col cols="9">
              <v-text-field
                  v-if="createMode"
                  label="输入新配置项名称"
                  v-model="newProfileName"
                  :rules="[v => !!v || '请输入名称']"
              >
              </v-text-field>
              <v-autocomplete
                  v-else
                  :items="$store.state.profiles"
                  item-text="name"
                  item-value="id"
                  label="点击以选择"
                  v-model="profileId"
                  ref="profileSelect"
                  :rules="[v => !!v || '请选择配置']"
                  auto-select-first
              >
              </v-autocomplete>
            </v-col>
            <v-col cols="3">
              <v-checkbox
                  v-model="createMode"
                  label="新建"
              ></v-checkbox>
            </v-col>
          </v-row>
        </v-form>
        <v-row justify="center">
          <span class="caption text--warma-dark">或者</span>
        </v-row>
        <v-divider class="mb-3"/>
        <v-row>
          <v-col cols="12">
            <v-text-field
                label="选择路径"
                dense
                v-model="profileFilePath"
                messages="从文件导入"
            >
              <template v-slot:append-outer>
                <v-btn
                    color="warma-dark"
                    outlined
                    @click="selectFilePath"
                    small
                    :disabled="isLoading"
                >
                  选择
                </v-btn>
              </template>
            </v-text-field>
          </v-col>
        </v-row>
      </v-container>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
            color="warma-dark"
            outlined
            @click="cancel"
            :disabled="isLoading"
        >
          取消
        </v-btn>
        <v-btn
            color="primary"
            outlined
            @click="select"
            :disabled="isLoading"
        >
          {{ createMode ? '创建' : '加载' }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import {Component, VModel, Vue} from "vue-property-decorator";
import {$Vue} from "@/main";
import {open} from "@tauri-apps/api/dialog";
import {appDir} from "@tauri-apps/api/path";
import {Profile} from "@/models/profile";
import {ExportProfileRequest} from "@/models/export-profile-request";
import {Message} from "@/components/MessageToast.vue";

@Component
export default class ProfileSelector extends Vue {
  @VModel()
  openDialog!: boolean
  profileId: number | null = null
  createMode = false;
  newProfileName = ''
  profileFilePath: string | null = null;
  isLoading = false;

  select() {
    if (this.profileFilePath == null && !(this.$refs.form as Vue & { validate: () => boolean }).validate()) return;
    this.isLoading = true;
    try {
      if (this.profileFilePath != null) this.loadFromFile();
      else if (this.createMode) this.createNew();
      else this.load();
    } finally {
      this.isLoading = false;
    }
  }

  async createNew() {
    $Vue.$emit('message', {message: '正在创建'} as Message);
    const newProfile: Profile = await this.$http.post('/profile', {
      name: this.newProfileName,
      active: true
    } as Profile).then(resp => resp.data);
    this.isLoading = false;
    this.openDialog = false;
    this.createMode = false;
    await this.$store.dispatch('switchProfile', newProfile.id);
    $Vue.$emit('message', {message: `创建成功！已切换到配置：${this.newProfileName}`} as Message);
    this.newProfileName = '';
  }

  load() {
    $Vue.$emit('open-reloader');
    this.isLoading = false;
    this.openDialog = false;
    $Vue.$emit('message', {message: '正在加载索引'} as Message);
    this.$store.dispatch('switchProfile', this.profileId).then(() => {
      $Vue.$emit('close-reloader');
      $Vue.$emit('message', {message: '已切换'} as Message);
    });
  }

  async loadFromFile() {
    $Vue.$emit('message', {message: '正在导入，并重建索引'} as Message);
    await this.$http.post('profile/import', {
      path: this.profileFilePath
    } as ExportProfileRequest);
    $Vue.$emit('message', {message: '导入成功！'} as Message);
    this.isLoading = false;
  }

  async selectFilePath() {
    const selected = await open({
      directory: false,
      filters: [{
        name: '导出的配置文件',
        extensions: ['json']
      }],
      defaultPath: await appDir(),
    });
    this.profileFilePath = selected as string;
  }

  cancel() {
    this.profileId = null;
    this.createMode = false;
    this.newProfileName = '';
    this.profileFilePath = null;
    this.isLoading = false;
    this.openDialog = false;
  }
}
</script>

<style scoped>

</style>
