<div class="container" id="app-create">


    <div class="input-group input-group-lg">
        <span class="input-group-addon" id="sizing-addon1">스케쥴 이름</span>
        <input type="text" v-model="jobDetail.name" class="form-control" placeholder="스케쥴 이름" aria-describedby="sizing-addon1">
    </div>

    <div class="input-group input-group-lg">
        <span class="input-group-addon" id="sizing-addon1">스케쥴 subject</span>
        <input type="text" v-model="jobDetail.subject" class="form-control" placeholder="스케쥴 subject" aria-describedby="sizing-addon1">
    </div>

    <div class="input-group input-group-lg">
        <span class="input-group-addon" id="sizing-addon1">스케쥴 messageBody</span>
        <input type="text" v-model="jobDetail.messageBody" class="form-control" placeholder="스케쥴 messageBody" aria-describedby="sizing-addon1">
    </div>

    <div class="input-group input-group-lg">
        <span class="input-group-addon" id="sizing-addon1">스케쥴 트리거 이름</span>
        <input type="text" v-model="jobDetail.triggers[0].name" class="form-control" placeholder="스케쥴 트리거 이름" aria-describedby="sizing-addon1">
    </div>

    <div class="input-group input-group-lg">
        <span class="input-group-addon" id="sizing-addon1">스케쥴 트리거 그룹</span>
        <input type="text" v-model="jobDetail.triggers[0].group" class="form-control" placeholder="스케쥴 트리거 그룹" aria-describedby="sizing-addon1">
    </div>

    <div class="input-group input-group-lg">
        <span class="input-group-addon" id="sizing-addon1">스케쥴 트리거 주기</span>
        <input type="text" v-model="jobDetail.triggers[0].cron" class="form-control" placeholder="스케쥴 트리거 주기" aria-describedby="sizing-addon1">
    </div>

    <button type="button" class="btn btn-default btn-lg" v-on:click="create">
        <span class="glyphicon glyphicon-save" aria-hidden="true"></span> Job 스케쥴 저장
    </button>


</div>