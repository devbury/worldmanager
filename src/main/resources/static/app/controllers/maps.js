app.controller('MapController', function ($scope, $rootScope, Map, $mdToast, $mdDialog) {

    $scope.maps = Map.query();

    $scope.deleteMap = function(map) {
        map.$delete().then(function() {
            $scope.maps = Map.query();
            $mdToast.show($mdToast.simple().textContent('Map Deleted'));
        });
    };

    $scope.showCreateMap = function (ev) {
        $mdDialog.show({
            controller: MapDialogController,
            scope: $scope,
            templateUrl: 'static/app/views/map-dialog.html',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: false,
            fullscreen: false
        });
    };

    function MapDialogController($scope, $mdDialog, $http, Map) {
        var formdata = new FormData();
        $scope.map = new Map();

        $scope.getTheFiles = function ($files) {
            angular.forEach($files, function (value, key) {
                formdata.append(key, value);
            });
        };

        $scope.uploadFiles = function () {
            $mdDialog.hide();
            $mdToast.show($mdToast.simple().textContent('Creating Map'));

            var request = {
                method: 'POST',
                url: '/api/map/' + $scope.map.name,
                data: formdata,
                headers: {
                    'Content-Type': undefined
                }
            };

            // SEND THE FILES.
            $http(request)
                .then(function (d) {
                    $scope.maps = Map.query();
                    $mdToast.show($mdToast.simple().textContent('Map Created'));
                }, function () {
                    $mdToast.show($mdToast.simple().textContent('Map Creation Failed'));
                });
        };

        $scope.cancel = function () {
            $mdDialog.cancel();
        };
    }
});