<template>
  <v-container style="height: 100vh">
    <v-row
        style="height: 100%;"
    >
      <v-col align-self="center" align="center">
        <v-sheet
            :elevation="24"
            :height="800"
            :width="1000"
            border
            color="amber-lighten-4"
            rounded="xl"
        >
          <br><br>
          <span style="font-size: x-large; font-style: italic;">编译器生成器</span>
          <div>
            <br>
            <v-textarea label="源代码" v-model="sourceCode"></v-textarea>
          </div>
          <div>
            <v-textarea label="词法规则" v-model="lexicalRules"></v-textarea>
            <v-btn @click="submitSourceCodeAndLexicalRules" color="light-blue" variant="outlined" size="large">提交</v-btn>
          </div>
          <div>
            <br><br>
            <v-textarea label="语法规则" v-model="grammarRules"></v-textarea>
            <v-btn @click="submitGrammarRules" color="light-blue" variant="outlined" size="large">提交</v-btn>
          </div>
        </v-sheet>
      </v-col>
    </v-row>
  </v-container>
  <v-snackbar
    v-model="showSnackbar"
    :timeout="3000"
    multi-line
    color="info"
    rounded="pill"
    >
    提交成功
  </v-snackbar>
</template>

<script setup>
import {ref} from 'vue'
import $ from 'jquery'

const sourceCode = ref("")
const lexicalRules = ref("")
const grammarRules = ref("")
const showSnackbar = ref(false)
const submitSourceCodeAndLexicalRules  = () => {
  console.log(lexicalRules.value)
  $.ajax({
    url: 'http://localhost:3005/process/lexical/rules/',
    type: 'post',
    data: {
      lexicalRules: lexicalRules.value,
      sourceCode: sourceCode.value
    },
    success(){
      showSnackbar.value = true
    },
    
  })
}

const submitGrammarRules = () => {
 console.log(grammarRules.value)
}
</script>

<style scoped>
</style>
