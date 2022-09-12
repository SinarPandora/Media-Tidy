<template>
  <v-dialog
      v-model="open"
      width="500px"
      persistent
  >
    <v-card tile outlined class="border--warma-dark bold-border" :loading="isLoading">
      <v-card-title class="warma-yellow text--warma-dark">
        编辑标签: {{ tag.name }}
      </v-card-title>

      <v-container fluid class="pa-3">
        <v-form ref="form" v-if="open">
          <v-row>
            <v-col cols="12">
              <v-text-field
                  label="路径"
                  v-model="tag.folder"
                  :rules="[v => !!v && v.trim().length > 0 || '该项为必填项']"
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
                  v-model="tag.name"
                  :rules="[v => v != null && v.length > 0 || '该项为必填项']"
                  messages="必填项"
              >
              </v-text-field>
            </v-col>
            <v-col cols="12">
              <v-textarea
                  label="备注"
                  dense
                  messages="可以不写"
                  v-model="tag.description"
                  outlined
              >
              </v-textarea>
            </v-col>
          </v-row>
        </v-form>
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
            color="error"
            outlined
            @click="deleteConfirm.open = true"
            :disabled="isLoading"
        >
          删除
        </v-btn>
        <v-btn
            color="primary"
            outlined
            @click="save"
            :disabled="isLoading"
        >
          保存
        </v-btn>
      </v-card-actions>
    </v-card>
    <!--删除确认-->
    <v-dialog v-model="deleteConfirm.open" width="300px" persistent>
      <v-card tile outlined class="border--warma-dark bold-border" :loading="deleteConfirm.isLoading">
        <v-card-title class="error text--warma-dark">
          确认删除
        </v-card-title>
        <v-container fluid class="pa-3">
          <span>确认删除标签：{{ tag.name }}?</span>
        </v-container>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="warma-dark" outlined @click="deleteConfirm.open = false; deleteConfirm.source = null"
                 :disabled="deleteConfirm.isLoading">
            取消
          </v-btn>
          <v-btn color="error" outlined @click.stop="deleteTag" :disabled="deleteConfirm.isLoading">
            删除
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-dialog>
</template>

<script lang="ts">
import {Component, Prop, VModel, Vue} from "vue-property-decorator";
import {Tag} from "@/models/tag";
import {open} from "@tauri-apps/api/dialog";
import {appDir} from "@tauri-apps/api/path";
import {$Vue} from "@/main";
import {Message} from "@/components/MessageToast.vue";

@Component
export default class TagEditPopup extends Vue {
  @VModel({required: true})
  tag!: Tag & { editing: boolean }
  @Prop({required: true})
  open!: boolean
  isLoading = false;
  deleteConfirm = {
    open: false,
    isLoading: false
  }

  async selectFilePath() {
    const selected = await open({
      directory: true,
      defaultPath: await appDir(),
    });
    this.tag.folder = selected as string;
  }

  cancel() {
    this.$emit('cancel');
  }

  async save() {
    if (!(this.$refs.form as Vue & { validate: () => boolean }).validate()) return;
    this.isLoading = true;
    try {
      await this.$http.post('/tag', [this.tag]);
      $Vue.$emit('message', {message: '保存成功！'} as Message);
      this.$emit('close');
    } finally {
      this.isLoading = false;
    }
  }

  async deleteTag() {
    this.deleteConfirm.isLoading = true;
    try {
      await this.$http.delete('tag/', {data: [this.tag.id]});
      $Vue.$emit('message', {message: '已删除！'} as Message);
      this.$emit('delete');
      this.deleteConfirm.open = false;
    } finally {
      this.deleteConfirm.isLoading = false;
    }
  }
}
</script>

<style scoped>

</style>
