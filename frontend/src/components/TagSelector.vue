<template>
  <v-card elevation="0" style="height: 100%" class="pa-2" ref="parentContainer">
    <div class="d-flex mt-1" v-if="!editMode">
      <v-btn small icon color="warma-dark" class="ml-3 border--warma-dark" @click.stop="tagEditorPopup.open = true">
        <v-icon>mdi-plus</v-icon>
      </v-btn>
      <v-btn small icon color="warma-dark" class="ml-3 border--warma-dark" @click.stop="editMode = true">
        <v-icon>mdi-pencil-outline</v-icon>
      </v-btn>
      <v-spacer/>
      <v-btn small text color="warma-dark" tile outlined class="ml-3 border--warma-dark bold-border"
             @click.stop="profileSelector.open = true">加载按钮设定
      </v-btn>
      <v-btn small text color="warma-dark" tile outlined class="ml-3 border--warma-dark bold-border"
             @click.stop="exportProfile">
        保存按钮设定
      </v-btn>
    </div>
    <!--退出编辑模式-->
    <div class="d-flex mt-1" v-else>
      <v-btn small icon color="warma-dark" class="ml-3 border--warma-dark" @click.stop="reloadTagAndIndex">
        <v-icon>mdi-content-save-outline</v-icon>
      </v-btn>
    </div>
    <v-card class="mt-1 tag-container" elevation="0" ref="container">
      <v-container fluid>
        <v-row>
          <v-col
              class="d-flex justify-center align-center tag-line"
              cols="3"
              v-for="(tag, idx) in tags" :key="idx"
          >
            <tag-btn :tag="tag" @click.native.stop="onClick(tag)" :edit-mode="editMode"></tag-btn>
          </v-col>
        </v-row>
      </v-container>
    </v-card>
    <tag-create-popup v-model="tagEditorPopup.open" @created="reload"/>
    <profile-selector v-model="profileSelector.open"/>
    <v-overlay absolute :value="isLoading">
      <v-progress-circular
          indeterminate
      ></v-progress-circular>
    </v-overlay>
  </v-card>
</template>

<script lang="ts">
import {Component, Prop, Vue} from "vue-property-decorator";
import {Tag} from "@/models/tag";
import TagCreatePopup from "@/components/TagCreatePopup.vue";
import TagBtn from "@/components/TagBtn.vue";
import ProfileSelector from "@/components/ProfileSelector.vue";
import {open} from "@tauri-apps/api/dialog";
import {appDir} from "@tauri-apps/api/path";
import {ExportProfileRequest} from "@/models/export-profile-request";
import {$Vue} from "@/main";
import {Message} from "@/components/MessageToast.vue";

@Component({
  components: {ProfileSelector, TagBtn, TagCreatePopup}
})
export default class TagSelector extends Vue {
  @Prop({required: true})
  isMediaSelected!: boolean;
  isLoading = false;
  tags: Tag[] = [];
  tagEditorPopup = {
    open: false
  }
  profileSelector = {
    open: false
  }
  editMode = false;

  // noinspection JSUnusedGlobalSymbols
  mounted() {
    this.load();
    this.$store.watch(state => state.tags, () => this.load());
    // TODO: Auto size
    // appWindow.onResized(async () => {
    //   const iSize = await appWindow.innerSize();
    //   const factor = await appWindow.scaleFactor();
    //   const size = iSize.toLogical(factor);
    //   (this.$refs.container as HTMLDivElement).style.maxHeight = `${size.height - 160 - 36}px`;
    // });
  }

  load() {
    this.isLoading = true;
    this.tags = this.$store.state.tags;
    this.isLoading = false;
  }

  reload() {
    this.isLoading = true;
    this.$store.dispatch('reloadTags');
  }

  onClick(tag: Tag) {
    if (!this.editMode) this.$emit('select', tag);
  }

  async exportProfile() {
    this.isLoading = true;
    try {
      const selected = await open({
        directory: true,
        defaultPath: await appDir(),
      });
      if (selected == null) return;
      await this.$http.post('/profile/export', {
        path: selected as string
      } as ExportProfileRequest);
      $Vue.$emit('message', {message: '导出成功！'} as Message);
    } finally {
      this.isLoading = false;
    }
  }

  async reloadTagAndIndex() {
    try {
      this.isLoading = true;
      await this.$http.post('/index/rebuild');
      await this.$store.dispatch('reloadAll');
      this.editMode = false;
    } finally {
      this.isLoading = false;
    }
  }
}
</script>

<style scoped>
.tag-container {
  max-height: calc(400px - 36px);
  overflow-y: scroll;
}

.tag-container::-webkit-scrollbar {
  display: none;
}

.tag-line {
  user-select: none;
  width: 100%;
  height: 100%;
  display: flex;
}
</style>
