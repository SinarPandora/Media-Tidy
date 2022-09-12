<template>
  <div class="d-flex justify-center align-center">
    <v-app>
      <v-app-bar
          app
          color="primary">
        <div class="d-flex align-center">
          <v-img
              alt="Icon"
              class="shrink mr-2"
              contain
              src="@/assets/icon.png"
              transition="scale-transition"
              width="40"
          />
          <div class="d-flex flex-column">
            <h3 class="text--warma-dark">
              Media Tidy
            </h3>
            <h5 class="text--warma-dark">
              音乐文件试听分类整理软件
            </h5>
          </div>
        </div>
        <v-spacer></v-spacer>
        <v-btn icon @click.stop="loadBasicData" color="warma-dark">
          <v-icon>mdi-reload</v-icon>
        </v-btn>
        <v-btn icon @click.stop="switchDayNight" color="warma-dark">
          <v-icon v-if="darkMode">mdi-weather-night</v-icon>
          <v-icon v-else>mdi-brightness-6</v-icon>
        </v-btn>
      </v-app-bar>
      <v-main>
        <router-view/>
      </v-main>
      <message-toast ref="toast"></message-toast>
      <v-overlay :value="isLoading">
        <v-progress-circular
            indeterminate
        ></v-progress-circular>
      </v-overlay>
    </v-app>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import Component from "vue-class-component";
import {ErrorMessage} from "@/models/error-message";
import {$Vue} from "@/main";
import MessageToast, {Message} from "@/components/MessageToast.vue";

@Component({
  components: {MessageToast}
})
export default class App extends Vue {
  darkMode = false;
  isLoading = false;

  // noinspection JSUnusedGlobalSymbols
  mounted() {
    this.isLoading = true;
    this.darkMode = window.matchMedia("(prefers-color-scheme:dark)").matches;
    this.$vuetify.theme.dark = this.darkMode;
    setTimeout(async () => {
      this.registerToast();
      await this.loadBasicData();
    });
  }

  switchDayNight() {
    this.darkMode = !this.darkMode;
    this.$vuetify.theme.dark = this.darkMode;
  }

  registerToast() {
    $Vue.$on('system-error',
        (data: ErrorMessage) =>
            (this.$refs.toast as MessageToast).showErrorMessage(data)
    );
    $Vue.$on('message', (data: Message) =>
        (this.$refs.toast as MessageToast).show(data)
    );
    $Vue.$on('open-reloader', () => this.isLoading = true);
    $Vue.$on('close-reloader', () => this.isLoading = false);
  }

  async loadBasicData() {
    this.isLoading = true;
    await this.$store.dispatch('loadProfileId');
    await this.$store.dispatch('switchProfile', this.$store.state.profileId);
    this.isLoading = false;
  }
}
</script>
