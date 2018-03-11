$(document).ready(function() {
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






