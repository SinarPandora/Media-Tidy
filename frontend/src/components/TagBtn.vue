<template>
  <v-card
      elevation="0"
      class="border--warma-dark bold-border v-btn tag"
      outlined
      ripple
      tile
      @click="onClick"
      :disabled="disabled || deleted"
  >
    <v-container
        fluid
        class="tag-name text--warma-dark"
    >
      <span :class="{ 'text-decoration-line-through': deleted }">{{ tag.name }}</span>
    </v-container>
    <v-overlay absolute :value="tag['isLoading'] ?? false">
      <v-progress-circular
          indeterminate
      ></v-progress-circular>
    </v-overlay>
    <tag-edit-popup v-model="tag" :open="open" @close="open = false" @delete="onDelete" ref="popup"/>
  </v-card>
</template>

<script lang="ts">
import {Component, Prop, Vue} from "vue-property-decorator";
import {Tag} from "@/models/tag";
import TagEditPopup from "@/components/TagEditPopup.vue";

@Component({
  components: {TagEditPopup}
})
export default class TagBtn extends Vue {
  @Prop({required: true})
  tag!: Tag
  @Prop({default: false})
  disabled!: boolean
  @Prop({default: false})
  editMode!: boolean
  isAnimating = false;
  open = false;
  deleted = false;

  onClick() {
    if (this.isAnimating) return;
    this.isAnimating = true;
    this.$el.classList.add('animate__animated', 'animate__rubberBand');
    // 业务逻辑开始
    if (this.editMode) {
      const backup = JSON.parse(JSON.stringify(this.tag)) as Tag;
      this.open = true;
      (this.$refs.popup as Vue).$once('cancel', () => {
        this.tag.name = backup.name;
        this.tag.folder = backup.folder;
        this.tag.description = backup.description;
        this.open = false;
      });
    }
    // 业务逻辑结束
    this.$el.addEventListener('animationend', (event) => {
      event.stopPropagation();
      this.$el.classList.remove('animate__animated', 'animate__rubberBand');
      this.isAnimating = false;
    }, {once: true});
  }

  onDelete() {
    this.deleted = true;
    this.open = false;
  }
}
</script>

<!--suppress CssUnresolvedCustomProperty -->
<style scoped>
.tag {
  cursor: pointer;
  flex: 1;
  border-radius: 2px !important;
}

.tag-name {
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  transition: .5s ease all;
}

.tag-name:hover {
  background-color: var(--v-warma-yellow-base);
}
</style>
