'use strict';
module.exports = function(grunt) {

    grunt.initConfig({
        'pkg' : grunt.file.readJSON('package.json'),

        copy : {
            jquery : {
                src : 'node_modules/jquery/dist/jquery.min.js',
                dest : 'src/main/resources/static/js/lib/jquery.min.js'
            },
            vuejs : {
                src : 'node_modules/vue/dist/vue.min.js',
                dest : 'src/main/resources/static/js/lib/vue.min.js'
            }
        },


        concat: {
            lib: {
                //순서 중요
                src:[
                    'src/main/resources/static/js/lib/jquery.min.js',
                    'src/main/resources/static/js/lib/vue.min.js'
                ],
                dest: 'src/main/resources/static/build/js/lib.js' //concat 결과 파일
            }
        },
    });


    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-concat');
    
    // Default task, grunt 명령어로 실행하는 작업
    grunt.registerTask('default', ['copy', 'concat']);
}
