module.exports = function (grunt) {
  grunt.initConfig({
    shell: {
      copy_resources:{
        command: 'cp -r ./src/main/webapp/* ./target/classes/src/main/webapp/.',
        options: {
            stdout: true
        }
      }
    },
    watch: {
      copy_resources: {
        files: ["./src/main/webapp/**"],
        tasks: ["shell:copy_resources"]
      }
    }
  });
  grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-shell');
};