<template>
  <a-layout-footer style="text-align: center">
    Ebook <span >，欢迎：{{user.name}}</span>
  </a-layout-footer>
</template>


<script lang="ts">
import store from '@/store';
import {computed, defineComponent } from 'vue';

export default defineComponent({
  name: 'TheFooter',
  setup (){
    const user = computed(() => store.state.user);
    return {
      user
    }
  }
});
</script>