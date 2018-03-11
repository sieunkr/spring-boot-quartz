

$(document).ready(function() {

    var appsList;

    $.ajax({
        url: "http://localhost:8082/groups/email/jobs/list",
        dataType: 'jsonp',
        success: function(data) {
            //appsList = data;
            console.log('성공 - ', data);

            appsList = data;
            app4.groceryList = appsList;
        },
        error: function(xhr) {
            console.log('실패 - ', xhr);
        }
    });

    Vue.component('todo-item', {
        // 이제 todo-item 컴포넌트는 "prop" 이라고 하는
        // 사용자 정의 속성 같은 것을 입력받을 수 있습니다.
        // 이 prop은 todo라는 이름으로 정의했습니다.
        props: ['todo'],
        template: '<a href="#" class="list-group-item" v-on:click="incrementCounter"><h4 class="list-group-item-heading">{{ todo.name }}</h4><p class="list-group-item-text">{{ todo.subject }}</p></a>',
        methods: {
            incrementCounter: function () {
                this.$emit('increment')
            }
        },
    })

    var app4 = new Vue({
        el: '#app-4',
        data: {
            groceryList: []
        },
        methods: {
            incrementTotal: function () {
                appDetail.jobDetail = appsList[0];
            }
        }
    })


    var appDetail = new Vue({
        el: '#appDetail',
        data: {
            jobDetail:{
                "name" : "-",
                "group" : "-",
                "subject" : "-",
                "messageBody" : "-"
            }
        },
        methods: {
        }
    })


    var appCreate = new Vue({
        el: '#app-create',
        data: {
            jobDetail:{
                "name" : "",
                "subject" : "",
                "messageBody" : "",
                "to" : ["123","456"],
                "triggers" : [
                    {
                        "name": "",
                        "group": "",
                        "cron": ""
                    }
                ]
            }
        },
        methods: {
            create: function () {

                debugger;

                /*
                {
                  "name": "스케쥴 이름",
                  "subject": "스케쥴 subject",
                  "messageBody": "스케쥴 messageBody",
                  "to": ["123", "456"],
                  "triggers":
                    [
                       {
                         "name": "스케쥴 이름",
                         "group": "스케쥴 그룹",
                         "cron": "0/5 * * * * ?"
                       }
                    ]
                }
                 */


                $.ajax({
                    url: "http://localhost:8082/groups/email/jobs",
                    crossDomain: true,
                    data: JSON.stringify( this.jobDetail ),
                    contentType: "application/json; charset=utf-8",
                    dataType: 'json',
                    type: "POST",
                    success: function(data) {
                        alert("스케쥴 생성");

                    },
                    error: function(xhr) {
                        console.log('실패 - ', xhr);
                    }
                });

            }
        }
    })





});






