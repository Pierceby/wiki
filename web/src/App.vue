<template>
  <a-layout>
    <TheHeader></TheHeader>
    <router-view/>
    <TheFooter></TheFooter>
  </a-layout>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import TheHeader from '@/components/TheHeader.vue';
import TheFooter from '@/components/TheFooter.vue';

export default defineComponent({
  name: 'app',
  components: {
    TheHeader,
    TheFooter,
  },
});
</script>