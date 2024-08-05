<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <a-row :gutter="24">
        <a-col :span="8">
          <p>
            <a-form layout="inline" :model="param">
              <a-form-item>
                <a-input v-model:value="param.name" placeholder="名称">
                </a-input>
              </a-form-item>
              <a-form-item>
                <a-button type="primary" @click="dectQuery()">
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
              v-if="docsTree.length > 0"
              :columns="columns"
              :row-key="record => record.id"
              :data-source="docsTree"
              :loading="loading"
              :pagination="false"
              size="small"
              :defaultExpandAllRows="true"

          >
            <template #name="{ text, record }">
              {{record.sort}} {{text}}
            </template>
            <template v-slot:action="{ text, record }">
              <a-space size="small">
                <a-button type="primary" @click="edit(record)" size="small">
                  编辑
                </a-button>
                <a-popconfirm
                    title="删除后不可恢复，确认删除?"
                    ok-text="是"
                    cancel-text="否"
                    @confirm="remove(record.id)"
                >
                  <a-button type="danger" size="small">
                    删除
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </a-table>
        </a-col>
        <a-col :span="16">
          <p>
            <a-form layout="inline" :model="param">
              <a-form-item>
                <a-button type="primary" @click="handleSave()">
                  保存
                </a-button>
              </a-form-item>
            </a-form>
          </p>
          <a-form :model="doc" layout="vertical">
            <a-form-item>
              <a-input v-model:value="doc.name" placeholder="名称"/>
            </a-form-item>
            <a-form-item>
              <a-tree-select
                  v-model:value="doc.parent"
                  style="width: 100%"
                  :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                  :tree-data="treeSelectData"
                  placeholder="请选择父文档"
                  tree-default-expand-all
                  :replaceFields="{title: 'name', key: 'id', value: 'id'}"
              >
              </a-tree-select>
            </a-form-item>
            <a-form-item>
              <a-input v-model:value="doc.sort" placeholder="顺序"/>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="handlePreviewContent()">
                <EyeOutlined /> 内容预览
              </a-button>
            </a-form-item>
            <a-form-item>
              <div id="content"></div>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>
      <a-drawer width="900" placement="right" :closable="false" :visible="drawerVisible" @close="onDrawerClose">
        <div class="wangeditor" :innerHTML="previewHtml"></div>
      </a-drawer>
    </a-layout-content>
  </a-layout>

<!--  <a-modal
      title="文档表单"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
  >
    <a-form :model="doc" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="名称">
        <a-input v-model:value="doc.name" />
      </a-form-item>
      <a-form-item label="父文档">
        <a-tree-select
            v-model:value="doc.parent"
            style="width: 100%"
            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
            :tree-data="treeSelectData"
            placeholder="请选择父文档"
            tree-default-expand-all
            :replaceFields="{title: 'name', key: 'id', value: 'id'}"
        >
        </a-tree-select>
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="doc.sort" />
      </a-form-item>
      <a-form-item label="内容">
        <div id="content"/>
      </a-form-item>
    </a-form>
  </a-modal>-->
</template>

<script lang="ts">
import { defineComponent,onMounted, ref } from 'vue';
import axios from 'axios' ;
import { message }  from "ant-design-vue";
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";
import E from 'wangeditor' ;

export default defineComponent({
  name: 'AdminDoc',
  setup(){
    const route = useRoute();
    const docs = ref();
    const docsTree = ref(); // 一级分类树，children属性就是二级分类
    docsTree.value = [] ;

    const param = ref();
    param.value = {};
    const loading = ref(false);
    const columns = [
      {
        title: '名称',
        dataIndex: 'name',
        slots: { customRender: 'name' }
      },
      {
        title: 'Action',
        key: 'action',
        slots: { customRender: 'action' }
      }
    ];
    // 因为树选择组件的属性状态，会随当前编辑的节点而变化，所以单独声明一个响应式变量
    const treeSelectData = ref();
    treeSelectData.value = [];
    
    /**
     * 数据查询
     */
    const handleQuery = () => {
      loading.value = true;
      axios.get("/doc/list/" + route.query.ebookId).then((response) => {
        loading.value = false;
        const data = response.data;
        if ( data.success ){
          docs.value = data.content ;
          console.log("原始数组：", docs.value);
          docsTree.value = [];
          // for (let i = 0; i < docs.value.length; i++) {
          //   docsTree.value.add(Tool.array2Tree(docs.value, 0));
          // }
          docsTree.value = Tool.array2Tree(docs.value, 0);
          console.log("树形结构：", docsTree);

          // 父文档下拉框初始化，相当于点击新增
          treeSelectData.value = Tool.copy(docsTree.value) || [];
          // 为选择树添加一个"无"
          treeSelectData.value.unshift({id: 0, name: '无'});
        }
        else {
          message.error(data.message);
        }
      });
    };

    /**
     * 精准数据查询
     */
    const dectQuery = () => {
      loading.value = true;
      axios.get("/doc/list", {
        params: {
          name: param.value.name ,
          ebookId: route.query.ebookId
        }
      }).then((response) => {
        loading.value = false;
        const data = response.data;
        if ( data.success ){
          docsTree.value = data.content ;
          if ( param.value.name == null ){
            docsTree.value = Tool.array2Tree(docsTree.value, 0);
          }
          param.value.name = null ;
        }
        else {
          message.error(data.message);
        }
      });
    };


    // -------- 表单 ---------
    const doc = ref();
    doc.value = {
      ebookId: route.query.ebookId
    };
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    // 创建富文本
    const editor = new E('#content');
    editor.config.zIndex = 0;

    const handleSave = () => {
      modalLoading.value = true;
      doc.value.content = editor.txt.html() ;
      axios.post("/doc/save", doc.value).then((response) => {
        const data = response.data; // data = commonResp
        modalLoading.value = false;
        if (data.success) {
          // modalVisible.value = false;
          message.success("保存成功！");
          // 重新加载列表
          handleQuery();
        }
        else {
          message.error(data.message) ;
        }
      });
    };

    /**
     * 将某节点及其子孙节点全部置为disabled
     */
    const setDisable = (treeSelectData: any, id: any) => {
      // console.log(treeSelectData, id);
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          // 如果当前节点就是目标节点
          console.log("disabled", node);
          // 将目标节点设置为disabled
          node.disabled = true;

          // 遍历所有子节点，将所有子节点全部都加上disabled
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              setDisable(children, children[j].id)
            }
          }
        } else {
          // 如果当前节点不是目标节点，则到其子节点再找找看。
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            setDisable(children, id);
          }
        }
      }
    };

    /**
     * 获取某节点及其子孙节点的全部id存放到数组idList中
     */
    const idList:Array<string> = [] ;
    const getIdList = (treeSelectData: any, id: any) => {
      // console.log(treeSelectData, id);
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          // 如果当前节点就是目标节点
          console.log("disabled", node);
          // 将目标节点设置为disabled
          idList.push(node.id) ;

          // 遍历所有子节点
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              getIdList(children, children[j].id)
            }
          }
        } else {
          // 如果当前节点不是目标节点，则到其子节点再找找看。
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            getIdList(children, id);
          }
        }
      }
    };

    /**
     * 内容查询
     **/
    const handleQueryContent = () => {
      axios.get("/doc/find-content/" + doc.value.id).then((response) => {
        const data = response.data;
        if (data.success) {
          editor.txt.html(data.content)
        } else {
          message.error(data.message);
        }
      });
    };
    /**
     * 编辑
     */
    const edit = (record: any) => {
      // 清空富文本框
      editor.txt.html("");
      modalVisible.value = true;
      doc.value = Tool.copy(record);
      handleQueryContent();
      // 不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
      treeSelectData.value = Tool.copy(docsTree.value);
      setDisable(treeSelectData.value, record.id);

      // 为选择树添加一个"无"
      treeSelectData.value.unshift({id: 0, name: '无'});
    };
    const add = () => {
      // 清空富文本框
      editor.txt.html("");
      modalVisible.value = true;
      doc.value = {
        ebookId: route.query.ebookId ,
      } ;
      treeSelectData.value = Tool.copy(docsTree.value) || [];
      // 为选择树添加一个"无"
      treeSelectData.value.unshift({id: 0, name: '无'});

    };
    const remove = (id: number) => {
      getIdList(docsTree.value,id);
      axios.delete("/doc/del/"+idList.join(",")).then((response) => {
        const data = response.data; // data = commonResp
        if (data.success) {
          // 重新加载列表
          handleQuery();
        }
      });
    };

    // ----------------富文本预览--------------
    const drawerVisible = ref(false);
    const previewHtml = ref();
    const handlePreviewContent = () => {
      const html = editor.txt.html();
      previewHtml.value = html;
      drawerVisible.value = true;
    };
    const onDrawerClose = () => {
      drawerVisible.value = false;
    };

    onMounted(() => {
      handleQuery();
      editor.create();
    });
    return{
      doc
      ,param
      ,docsTree
      ,columns
      ,loading
      ,handleQuery
      ,dectQuery
      ,edit
      ,add
      ,remove
      ,modalVisible
      ,modalLoading
      ,handleSave
      ,treeSelectData
      ,drawerVisible
      ,previewHtml
      ,handlePreviewContent
      ,onDrawerClose
    }
  }
});
</script>

<style scoped>
img {
  width: 50px;
  height: 50px;
}
</style>