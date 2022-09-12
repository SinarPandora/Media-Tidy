<template>
  <v-dialog
      v-model="openDialog"
      width="500px"
      persistent
  >
    <v-card tile outlined class="border--warma-dark bold-border" :loading="isLoading">
      <v-card-title class="warma-yellow text--warma-dark">
        创建标签
      </v-card-title>

      <v-container fluid>
        <v-form class="pa-3" ref="form" v-if="openDialog">
          <v-row>
            <v-col cols="12">
              <v-text-field
                  label="路径"
                  dense
                  v-model="data.folder"
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
                  label="名称"
                  dense
                  messages="必填项"
                  v-model="data.name"
                  :rules="[v => !!v && v.trim().length > 0]"
              >
              </v-text-field>
            </v-col>
            <v-col cols="12">
              <v-textarea
                  label="备注"
                  dense
                  messages="可以不写"
                  v-model="data.description"
                  outlined
              >
              </v-textarea>
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
        >
          取消
        </v-btn>
        <v-btn
            color="primary"
            outlined
            @click="submit"
        >
          创建
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import {Component, VModel, Vue} from "vue-property-decorator";
import {open} from "@tauri-apps/api/dialog";
import {appDir} from "@tauri-apps/api/path";

export interface NewTag {
  folder: string | null
  name: string
  description: string
  notIndex: boolean,
  menuOrder: number
}

@Component
export default class TagCreatePopup extends Vue {
  @VModel({required: true})
  openDialog!: boolean;
  data: NewTag = {
    folder: null,
    name: '',
    description: '',
    notIndex: false,
    menuOrder: 0
  }
  isLoading = false;

  async selectFilePath() {
    const selected = await open({
      directory: true,
      defaultPath: await appDir(),
    });
    this.data.folder = selected as string;
  }

  async submit() {
    if (!(this.$refs.form as Vue & { validate: () => boolean }).validate()) return;
    this.isLoading = true;
    try {
      await this.$http.post('/tag/', [this.data]);
      this.$emit('created');
      this.openDialog = false;
      this.data = {
        folder: null,
        name: '',
        description: '',
        notIndex: false,
        menuOrder: 0
      }
    } finally {
      this.isLoading = false;
    }
  }

  cancel() {
    this.data = {
      folder: null,
      name: '',
      description: '',
      notIndex: false,
      menuOrder: 0
    }
    this.openDialog = false;
  }
}
</script>
