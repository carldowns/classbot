// https://www.sitepoint.com/fetching-data-third-party-api-vue-axios/
// ./app.js

//const vm = new Vue({
//    el: '#app',
//    data: {
//        results: [
//            {title: "the very first post", abstract: "lorem ipsum some test dimpsum"},
//            {title: "and then there was the second", abstract: "lorem ipsum some test dimsum"},
//            {title: "third time's a charm", abstract: "lorem ipsum some test dimsum"},
//            {title: "four the last time", abstract: "lorem ipsum some test dimsum"}
//        ]
//    }
//});

const vm = new Vue({
    el: '#app',
    data: {
        results: [],
        newCourseNumber : null,
        newSemesterTitle : null,
        siteUser: null,
        sitePass: null,
        emailUser: null,
        emailPass: null,
        phoneNumber: null
    },
    //mounted() {
    //    axios.get("api/main")
    //        .then(response => {
    //            this.results = response.data;
    //            //console.log(this.results);
    //        })
    //        .catch((error) => {
    //            console.log(error);
    //        })
    //},

    created: function() {
        this.fetchList();
        //this.timer = window.setInterval(vm.fetchList(), 2000); // 1 seconds, refresh
    },

    watch: {
    },
    filters: {
    },
    computed: {
    },
    methods: {
        setSiteCredentials() {
            axios({
                method: 'post',
                url: '/api/main/site',
                headers: {
                    'Content-type': 'application/x-www-form-urlencoded'
                },
                params: {
                    siteUser: this.siteUser,
                    sitePass: this.sitePass
                }
            })
                .then((response) => {
                    console.log(response);
                })
                .catch((error) => {
                    console.log(error);
                }
            );
        },

        setNotificationCredentials() {
            axios({
                method: 'post',
                url: '/api/main/notification',
                headers: {
                    'Content-type': 'application/x-www-form-urlencoded'
                },
                params: {
                    emailUser: this.emailUser,
                    emailPass: this.emailPass,
                    phoneNumber: this.phoneNumber
                }
            })
                .then((response) => {
                    console.log(response);
                })
                .catch((error) => {
                    console.log(error);
                }
            );
        },

        toggleEnabled(courseNumber) {
            this.toggle(courseNumber, "enabled")},

        toggleNotifyWhenOpen(courseNumber) {
            this.toggle(courseNumber, "notifyWhenOpen")},

        toggleNotifyWhenClosed(courseNumber) {
            this.toggle(courseNumber, "notifyWhenClosed")},

        toggleNotifyWhenWaitListed(courseNumber) {
            this.toggle(courseNumber, "notifyWhenWaitListed")},

        toggle(courseNumber, propertyType) {
            console.log("toggle type " + propertyType + " for course " + courseNumber);
            axios.put('/api/main/course/' + courseNumber + '/toggle/' + propertyType, {
            })
                .then(function (response) {
                    console.log(response);
                    vm.fetchList();
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        fetchList() {
            axios.get("api/main")
                .then(response => {
                    this.results = response.data;
                })
                .catch((error) => {
                    console.log(error);
                })
        },

        // workaround with query parameters
        addCourse () {
            axios({
                method: 'post',
                url: '/api/main/course',
                headers: {
                    'Content-type': 'application/x-www-form-urlencoded'
                },
                params: {
                    semesterTitle: this.newSemesterTitle,
                    courseNumber: this.newCourseNumber
                }
            })
                .then((response) => {
                    console.log(response);
                    vm.fetchList();
                    this.newCourseNumber = null;
                })
                .catch((error) => {
                    console.log(error);
                }
            );
        },

        deleteCourse (courseNumber) {
            axios.delete('/api/main/course/' + courseNumber, {
            })
                .then(function (response) {
                    console.log(response);
                    vm.fetchList();
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        runNow (courseNumber) {
            axios.get('/api/main/run/' + courseNumber, {
            })
                .then(function (response) {
                    console.log(response);
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        refreshDisplay() {
            vm.fetchList();
        }

    },
    directives: {
    }
});
