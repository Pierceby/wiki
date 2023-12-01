<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-button type="primary" @click="handleQuery()">
              查询
            </a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="add()">
              新增
            </a-button>
          </a-form-item>
        </a-form>
      </p>
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="level1"
          :pagination="false"
          :loading="loading"
      >
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="showModal(record)">
              编辑
            </a-button>
            <a-popconfirm
                title="删除后不可恢复，是否删除？"
                ok-text="是"
                cancel-text="否"
                @confirm="handleDelete(record.id)"
            >
              <a-button type="danger">
                删除
              </a-button>
            </a-popconfirm>

          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>
  <a-modal
      v-model:open="open"
      title="分类表单"
      :confirm-loading="confirmLoading"
      @ok="handleOk">
    <a-form :model="category" :label-col="{ span:6}" :wrapper-col="{ span: 18 }">
      <a-form-item label="名称">
        <a-input v-model:value="category.name" />
      </a-form-item>
      <a-form-item label="父分类">
        <a-select
            ref="select"
            v-model:value="category.parent"
        >
          <a-select-option value="0">无</a-select-option>
          <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="category.id === c.id">
            {{ c.name }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="category.sort" />
      </a-form-item>
    </a-form>

  </a-modal>
</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from 'axios';
import {message} from 'ant-design-vue'
import { Tool } from '@/util/tool';

export default defineComponent({
  name: 'AdminCategory',
  setup() {
    const param = ref();
    param.value = {};
    const categorys = ref();
    const loading = ref(false);

    /**
     * 一级分类树，children属性就是二级分类
     * [{
     *   id: "",
     *   name: "",
     *   children: [{
     *     id: "",
     *     name: "",
     *   }]
     * }]
     */
    const level1 = ref(); // 一级分类树，children属性就是二级分类
    level1.value = [];

    const columns = [
      {
        title: '名称',
        dataIndex: 'name'
      },
      {
        title: '父分类',
        key: 'parent',
        dataIndex: 'parent'
      },
      {
        title: '顺序',
        dataIndex: 'sort'
      },
      {
        title: 'Action',
        key: 'action',
        slots: {customRender: 'action'}
      }
    ];
    const handleQuery = () => {
      loading.value = true;
      axios.get("/category/all").then((response) => {
        loading.value = false;
        const data = response.data;
        if(data.success){
          categorys.value = data.content;
          console.log("原始数组：",categorys.value);
          level1.value = [];
          level1.value = Tool.array2Tree(categorys.value, 0);
          console.log("树形结构：", level1);
        }else {
          message.error(data.message);
        }
      });
    };
    /**
     * 表格点击页码时触发
     */
    onMounted(() => {
      handleQuery();
    });

    const open = ref<boolean>(false);
    const confirmLoading = ref<boolean>(false);
    const modalText = ref<string>('Content of the modal');
    const category=ref({});

    const showModal = (record:any) => {
      open.value = true;
      category.value=Tool.copy(record);
    };

    const handleOk = () => {
      confirmLoading.value = true;
      axios.post("/category/save",category.value).then((response) => {
        confirmLoading.value = false;
        const data = response.data;
        if(data.success){
          open.value = false;
          handleQuery();
        }else {
          message.error(data.message);
        }
      });
    };
    const add=()=>{
      open.value = true;
      category.value={};
    }

    const handleDelete=(id:number)=>{
      console.log(id);
      axios.delete("/category/delete/"+id).then((response) => {
        const data = response.data;
        if(data.success){
          //重新加载列表
          handleQuery();
        }
      });
    }

    return {
      param,
      columns,
      loading,
      confirmLoading,
      modalText,
      handleOk,
      open,
      add,
      handleDelete,
      showModal,
      handleQuery,
      category,
      level1
    }
  }
});
</script>