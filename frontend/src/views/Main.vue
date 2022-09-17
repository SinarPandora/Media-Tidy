<template>
  <v-container fluid style="height: calc(100vh - 100px); min-width: 960px">
    <v-row style="height: 160px; position: sticky">
      <v-col>
        <player :file="selectedFile"/>
      </v-col>
    </v-row>
    <v-row style="height: calc(100vh - 240px); overflow-y: hidden; min-height: 374px;">
      <v-col cols="6" lg="3">
        <source-list @select="onMediaSelect"/>
      </v-col>
      <v-col cols="6" lg="9">
        <tag-selector @select="onTagSelect" :is-media-selected="this.selectedFile != null"/>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import Vue from "vue";
import Component from "vue-class-component";
import Player from "@/components/Player.vue";
import SourceList from "@/components/SourceList.vue";
import TagSelector from "@/components/TagSelector.vue";
import {MediaFile} from "@/models/media-file";
import {Tag} from "@/models/tag";
import {TidyRequest} from "@/models/tidy-request";
import {Message} from "@/components/MessageToast.vue";
import {$Vue} from "@/main";

@Component({
  components: {TagSelector, SourceList, Player}
})
export default class Main extends Vue {
  selectedFile: MediaFile | null = null;
  isLoading = false;

  onMediaSelect(file: MediaFile) {
    this.selectedFile = file;
  }

  async onTagSelect(tag: Tag) {
    if (this.selectedFile == null || this.isLoading) return;
    try {
      const request = {
        fileId: this.selectedFile.id,
        tagId: tag.id
      } as TidyRequest
      (tag as unknown as { isLoading: boolean }).isLoading = true;
      await this.$http.put('/tidy/', request)
          .then(() => $Vue.$emit('message', {
            message: `已将 ${this.selectedFile?.filename} 移动到 ${tag.name}`,
            status: 'success'
          } as Message));
      await this.$store.commit('tidyFile', this.selectedFile);
      (tag as unknown as { isLoading: boolean }).isLoading = false;
      await $Vue.$emit('tidied', this.selectedFile);
    } finally {
      this.isLoading = false;
    }
  }
}
</script>

