<template>
    <div class="assign-form-outer-container">
      <div class="assign-form-container">
        <div class="name-desc-assignment">
          <h3>{{name}}</h3>
          <p>{{description}}</p>
        </div>
       <div class="rest-of-assignment">
        <p v-if='submitted'>This assignment has already been submitted!</p>
        <form v-on:submit.prevent='submit()'>
          <div v-for="(question, index) in questions" v-bind:key='index'>
            <h4>Question {{index + 1}} ({{question.weight}} pts):</h4>
            <p>{{question.statement}}</p>
            <div v-if="question.type=='text'" class="text-answer">
              <textarea :disabled='submitted' v-model="answers[index]" v-if='$store.state.user.authorities[0]["name"]=="ROLE_USER"'></textarea>
            </div>
            <div v-else class="mc-answer">
              <div v-for="(answer, aindex) in question.answers" v-bind:key='aindex' v-bind:class="{ 'selected':answer.selected }" v-on:click.prevent='selectAnswer(index, aindex)'>
                <p>{{answer.text}}</p>
              </div>
            </div>
            <p v-if='$store.state.user.authorities[0]["name"]=="ROLE_TEACHER"'>Answer: {{question.correct}}</p>
              <br/>
              <br/>
          </div>
          <div v-if='$store.state.user.authorities[0]["name"]=="ROLE_USER"'>
            <button class="hw-button" :disabled='submitted' v-on:click.prevent=clear()>Clear Answers</button>
            <button class="hw-button" :disabled='submitted' v-on:click.prevent=save()>Save Progress</button>
            <button class="hw-button" :disabled='submitted' type=submit>Submit Homework</button>
          </div>
        </form> 
       </div>
      </div>
    </div>
</template>

<script>
  import homeworkService from "../services/HomeworkService";

  export default {
    name: "assignment-form",
    data() {
        return {
          name: '',
          description: '',
          dueDate: '',
          questions: [],
          answers: [],
          submitted: false
        }
    },
    props: ["id"],
    methods: {
      clear(){
        this.questions.forEach((q)=>{q.answers.forEach((a)=>{a.selected=false;});});
        this.answers = this.answers.map(()=>'');
      },
      save(){
        for(let i=0;i<this.answers.length;i++){
          homeworkService.saveHomeworkProgress(this.id,i+1,this.answers[i]);
        }
      },
      submit(){
        this.save();
        homeworkService.submitHomework(this.id);
        this.$router.push('/homework');
      },
      selectAnswer(q, a){
        if(!this.submitted && this.$store.state.user.authorities[0]["name"]=="ROLE_USER"){
          if(this.questions[q].type == 'mc'){
            this.answers[q]=a;
            this.questions[q].answers.forEach((a)=>{a.selected=false;});
            this.questions[q].answers[a].selected=true;
          }else{
            this.questions[q].answers[a].selected=!this.questions[q].answers[a].selected;
            this.answers[q] = this.questions[q].answers.reduce((acc,val,i)=>{return val.selected?acc+','+i:acc;},'');
          }
        }
      }
    },
    mounted() {
      homeworkService.get(this.id).then(response=>{
        this.name = response.data.name;
        this.description = response.data.description;
        this.dueDate = response.data.dueDate;
        this.questions = response.data.questions.map((question)=>{
          let q = {
            'type': question.type,
            'statement': question.statement,
            'answers': question.answers.map((a)=>{return {'text':a,'selected':false};}),
            'weight': question.weight,
            'correct': question.type=='text'?question.answers[0]:question.correct.reduce((acc,val,i)=>{return val?acc+','+(question.answers[i]):acc;},'').substring(1)
          };
          if(question.type == 'text'){q.answers = [];}
          return q;
        });
        this.clear();
        this.answers = response.data.answers;
        for(let i=0;i<this.answers.length;i++){
          if(this.questions[i].type!='text' && this.answers[i] != ''){
            this.answers[i].split(',').forEach((j)=>{this.questions[i].answers[j].selected=true;});
          }
        }
      });
      homeworkService.submitted(this.id).then(response=>{
        if(response.data!='unsubmitted'){
          this.submitted=true;
        }
      });
    }
  }
</script>

<style>
.selected{
  color:white;
}

.rest-of-assignment{
  grid-area: therest;
  display:flex;
  text-align: left;
  
}

.name-desc-assignment{
  display: flex;
  flex-direction: column;
 text-align: center;
  width: 100%;
  align-content: center;
  grid-area: namedesc;
}

.assign-form-container{
  display: grid;
  grid-template-columns: 1fr;
  grid-template-areas: 
  "namedesc"
  "therest";
  
  grid-area: wholething;
  background-color:rgb(175, 198, 216);
  border: solid black;
  padding: 0.5rem 1rem;
  
}

.assign-form-outer-container{
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  grid-template-areas: 
  ". namedesc ."
  ". wholething .";
}

.mc-answer p:hover{
  cursor: pointer;
}

.hw-button{
  margin-right: 3px;
}
</style>