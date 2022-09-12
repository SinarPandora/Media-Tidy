<template>
  <v-card class="border--warma-dark bold-border" outlined style="height: 100%">
    <v-container fluid>
      <v-row>
        <v-col cols="12">
          <v-row>
            <v-col cols="4">
              <p class="text-h6 file-title">{{ file?.filename ?? '未选择文件' }}</p>
            </v-col>
            <v-col cols="8">
              <audio
                  style="width: 100%"
                  controls
                  ref="audioPlayer"
              >
              </audio>
            </v-col>
          </v-row>
        </v-col>
        <v-col cols="12" class="d-flex pt-0 pb-0 align-center">
          <v-btn icon outlined tile class="border--warma-dark bold-border text--warma-dark" large
                 @click.stop="playOrPause">
            <v-icon v-if="isPlaying">mdi-pause</v-icon>
            <v-icon v-else>mdi-play</v-icon>
          </v-btn>
          <v-btn icon outlined tile class="border--warma-dark bold-border text--warma-dark ml-3" large
                 @click.stop="stop">
            <v-icon>mdi-stop</v-icon>
          </v-btn>
          <v-btn icon outlined tile class="border--warma-dark bold-border text--warma-dark ml-3" large
                 @click.stop="skipPrev">
            <v-icon>mdi-skip-previous</v-icon>
          </v-btn>
          <v-btn icon outlined tile class="border--warma-dark bold-border text--warma-dark ml-3" large
                 @click.stop="skipNext">
            <v-icon>mdi-skip-next</v-icon>
          </v-btn>
          <v-spacer/>
          <v-slider
              color="warma-dark"
              track-color="warma-dark"
              hide-details
              v-model="volume"
              max="1"
              step="0.1"
              @input="player.volume = volume"
          >
            <template v-slot:prepend>
              <v-icon color="warma-dark" v-if="volume === 0">mdi-volume-variant-off</v-icon>
              <v-icon color="warma-dark" v-else-if="volume < 0.5">mdi-volume-low</v-icon>
              <v-icon color="warma-dark" v-else-if="volume < 0.7">mdi-volume-medium</v-icon>
              <v-icon color="warma-dark" v-else>mdi-volume-high</v-icon>
            </template>
          </v-slider>
          <v-spacer/>
          <v-slider
              color="warma-dark"
              track-color="warma-dark"
              hide-details
              v-model="speed"
              max="2"
              min="0.5"
              step="0.5"
              @input="player.playbackRate = speed"
          >
            <template v-slot:prepend>
              <v-icon color="warma-dark" v-if="speed === 0.5">mdi-seat-passenger</v-icon>
              <v-icon color="warma-dark" v-else-if="speed === 1">mdi-run</v-icon>
              <v-icon color="warma-dark" v-else-if="speed === 1.5">mdi-run-fast</v-icon>
              <v-icon color="warma-dark" v-else>mdi-bike-fast</v-icon>
            </template>
          </v-slider>
          <v-spacer/>
          <v-checkbox color="warma-red" @change="autoPlay => player.autoplay = autoPlay">
            <template v-slot:label>
              <span class="font-weight-bold text--warma-dark">
                自动开始播放音乐
              </span>
            </template>
          </v-checkbox>
        </v-col>
      </v-row>
    </v-container>
  </v-card>
</template>

<script lang="ts">
import {Component, Prop, Vue, Watch} from "vue-property-decorator";
import {MediaFile} from "@/models/media-file";
import {$Vue} from "@/main";
import {path} from "@tauri-apps/api";

@Component
export default class Player extends Vue {
  @Prop({required: true})
  file?: MediaFile | null
  player!: HTMLAudioElement
  isPlaying = false;
  volume = 1;
  speed = 1;

  // noinspection JSUnusedGlobalSymbols
  mounted() {
    this.player = (this.$refs.audioPlayer as HTMLAudioElement);
    $Vue.$on('tidied', () => this.stop());
  }

  playOrPause() {
    if (this.file == null) return;
    if (this.isPlaying) this.player.pause();
    else this.player.play();
    this.isPlaying = !this.isPlaying;
  }

  stop() {
    if (this.file == null) return;
    this.player.pause();
    this.player.fastSeek(0);
    this.isPlaying = false;
  }

  skipPrev() {
    $Vue.$emit('selectPrev');
  }

  skipNext() {
    $Vue.$emit('selectNext');
  }

  @Watch('file')
  async onLoad() {
    if (this.file == null) return;
    const filepath = await path.join(this.file.folder, this.file.filename)
    this.player.src = `http://localhost:9010/tidy/preview${filepath}`;
    if (this.player.autoplay) this.isPlaying = true;
  }
}
</script>

<style scoped>
.file-title {
  white-space: nowrap;
  text-overflow: ellipsis;
  max-width: 40ch;
  overflow: hidden;
}

</style>
