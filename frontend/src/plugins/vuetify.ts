import Vue from 'vue';
import Vuetify from 'vuetify/lib/framework';

Vue.use(Vuetify);

export default new Vuetify({
    theme: {
        themes: {
            light: {
                primary: '#70AFCC',
                secondary: '#4C566A',
                info: '#50BFAB',
                warning: '#FDDE7F',
                error: '#EB7267',
                'warma-dark': '#523232',
                'warma-red': '#EB7267',
                'warma-yellow': '#FDDE7F',
            },
            dark: {
                primary: '#B48EAD',
                secondary: '#D8DEE9',
                info: '#A3BE8C',
                warning: '#EBCB8B',
                error: '#BF616A',
                'warma-dark': '#D8DEE9',
                'warma-red': '#5E81AC',
                'warma-yellow': '#3B4252',
            }
        },
        options: {customProperties: true},
    }
});
