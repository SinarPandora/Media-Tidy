<template>
  <v-dialog
      v-model="openDialog"
      width="500px"
      persistent
  >
    <v-card tile outlined class="border--warma-dark bold-border" :loading="isLoading">
      <v-card-title class="warma-yellow text--warma-dark">
        导入文件夹
      </v-card-title>

      <v-container fluid>
        <v-form class="pa-3" ref="form" v-if="openDialog">
          <v-row>
            <v-col cols="12">
              <v-text-field
                  label="路径"
                  dense
                  v-model="data.filepath"
                  :rules="[v => !!v && v.trim().length > 0]"
                  messages="必填项"
              >
                <template v-slot:append-outer>
                  <v-btn
                      color="warma-dark"
                      outlined
                      @click="selectFilePath"
                      small
                  >
                    选择
                  </v-btn>
                </template>
              </v-text-field>
            </v-col>
            <v-col cols="12">
              <v-text-field
                  label="别名"
                  dense
                  messages="可以不写"
                  v-model="data.alias"
              >
              </v-text-field>
            </v-col>
          </v-row>
        </v-form>
      </v-container>

      <v-divider></v-divider>

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
            @click="submit"
            :disabled="isLoading"
        >
          导入
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import {Component, VModel, Vue} from "vue-property-decorator";
import {open} from "@tauri-apps/api/dialog";
import {appDir} from "@tauri-apps/api/path";
import {Message} from "./MessageToast.vue";
import {$Vue} from "@/main";

export interface NewSource {
  filepath: string | null;
  alias: string;
}

@Component
export default class ImportSourcePopup extends Vue {
  @VModel({required: true})
  openDialog!: boolean;
  data: NewSource = {
    filepath: null,
    alias: ''
  }
  isLoading = false;

  async selectFilePath() {
    const selected = await open({
      directory: true,
      defaultPath: await appDir(),
    });
    this.data.filepath = selected as string;
  }

  submit() {
    if ((this.$refs.form as Vue & { validate: () => boolean }).validate()) {
      this.isLoading = true;
      $Vue.$emit('message', {message: '正在扫描源并构建索引'} as Message);
      this.$http.post('/source', [this.data]).then(async () => {
        this.$emit('created');
        await this.$store.dispatch('reloadSources');
        $Vue.$emit('message', {message: '创建成功！'} as Message);
        this.openDialog = false;
        this.data = {
          filepath: null,
          alias: ''
        };
      }).finally(() => this.isLoading = false);
    }
  }

  cancel() {
    this.openDialog = false
    this.data = {
      filepath: null,
      alias: ''
    };
  }
}
</script>
