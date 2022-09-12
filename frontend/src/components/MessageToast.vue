<template>
  <div style="z-index: 300;">
    <v-snackbar v-model="showSnackbar" :timeout="status === 'success'? 3000: 6000" right top :color="status">
      <span class="font-weight-bold">{{ message }}</span>
      <template v-slot:action="{ attrs }">
        <v-btn color="white" text v-bind="attrs" @click="showSnackbar = false">
          关闭
        </v-btn>
      </template>
    </v-snackbar>
  </div>
</template>

<script lang="ts">
import {Component, Vue} from "vue-property-decorator";
import {ErrorMessage} from "@/models/error-message";

export interface Message {
  status: 'success' | 'error' | 'warning' | 'info' | null
  message: string
}

@Component
export default class MessageToast extends Vue {
  private showSnackbar = false;
  private message = '';
  private status: 'success' | 'error' | 'warning' | 'info' = 'success';

  show(message: Message) {
    this.message = message.message;
    this.status = message.status?? 'success'
    this.showSnackbar = true;
  }

  showSuccessMessage(message: string) {
    this.message = message;
    this.status = 'success';
    this.showSnackbar = true;
  }

  showWarningMessage(message: string) {
    this.message = message;
    this.status = 'warning';
    this.showSnackbar = true;
  }

  showErrorMessage(message: string | ErrorMessage) {
    if (typeof message === 'string') {
      this.message = message;
    } else {
      this.message = (message.known? '' : '[未知错误] ') + message.message
    }
    this.status = 'error';
    this.showSnackbar = true;
  }

  showInfoMessage(message: string) {
    this.message = message;
    this.status = 'info';
    this.showSnackbar = true;
  }
}
</script>

<style scoped>

</style>
