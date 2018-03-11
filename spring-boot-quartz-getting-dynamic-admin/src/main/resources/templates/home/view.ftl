<div class="container">
    <div class="row">
        <div class="col-md-3" id="app-4">
            <div class="list-group">
                <todo-item
                        v-for="item in groceryList"
                        v-bind:todo="item"
                        v-bind:key="item.id"
                        v-on:increment="incrementTotal">
                </todo-item>
            </div>
        </div>
        <div class="col-md-7" id="appDetail">

            <div class="input-group input-group-lg">
                <span class="input-group-addon" id="sizing-addon1">스케쥴 이름</span>
                <input type="text" class="form-control" v-bind:value="jobDetail.name" placeholder="스케쥴 이름" aria-describedby="sizing-addon1">
            </div>

            <div class="input-group input-group-lg">
                <span class="input-group-addon" id="sizing-addon1">스케쥴 그룹</span>
                <input type="text" class="form-control" v-bind:value="jobDetail.group" placeholder="스케쥴 group" aria-describedby="sizing-addon1">
            </div>

            <div class="input-group input-group-lg">
                <span class="input-group-addon" id="sizing-addon1">스케쥴 subject</span>
                <input type="text" class="form-control" v-bind:value="jobDetail.subject" placeholder="스케쥴 subject" aria-describedby="sizing-addon1">
            </div>

            <div class="input-group input-group-lg">
                <span class="input-group-addon" id="sizing-addon1">스케쥴 messageBody</span>
                <input type="text" class="form-control" v-bind:value="jobDetail.messageBody" placeholder="스케쥴 messageBody" aria-describedby="sizing-addon1">
            </div>

            <div v-if="jobDetail.triggerState === 'NORMAL'">
                <button type="button" class="btn btn-default btn-lg">
                    <span class="glyphicon glyphicon-pause" aria-hidden="true"></span> Job 스케쥴 중지하기
                </button>
            </div>
            <div v-else-if="jobDetail.triggerState === 'PAUSE'">
                <button type="button" class="btn btn-default btn-lg">
                    <span class="glyphicon glyphicon-start" aria-hidden="true"></span> Job 스케쥴 시작하기
                </button>
            </div>
            <div v-else>
                ??? 알수 없음
            </div>

            <button type="button" class="btn btn-default btn-lg">
                <span class="glyphicon glyphicon-edit" aria-hidden="true"></span> Job 스케쥴 수정
            </button>

        </div>
    </div>

</div>