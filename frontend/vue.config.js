const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: [
    'vuetify'
  ],
  devServer: {
    port: 4000,
    proxy: {
      '^/(history|index|profile|setting|tag|source|tidy)': {
        target: 'http://127.0.0.1:9010',
        changeOrigin: true,
      }
    }
  }
})
