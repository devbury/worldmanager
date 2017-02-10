app.controller('ServerController', function ($scope, $rootScope, Server, $mdToast, $mdDialog) {
    $scope.servers = Server.query();
    $scope.versions = $rootScope.versions;

    $scope.serverDoesNotExist = function (name) {
        return !$scope.servers.map(function (e) {
            return e.name
        }).includes(name);
    };

    $scope.stopServer = function (server) {
        $mdToast.show($mdToast.simple().textContent('Stopping Server'));
        server.$stop().then(function successCallback(response) {
            $mdToast.show($mdToast.simple().textContent('Server Stopped'));
            $scope.servers = Server.query();
        }, function errorCallback(response) {
            $mdToast.show($mdToast.simple().textContent('Problem Stopping Server'));
            $scope.servers = Server.query();
        });
    };

    $scope.startServer = function (server) {
        $mdToast.show($mdToast.simple().textContent('Starting Server'));
        server.$start().then(function successCallback(response) {
            $scope.servers = Server.query();
        }, function errorCallback(response) {
            $mdToast.show($mdToast.simple().textContent('Problem Starting Server'));
            $scope.servers = Server.query();
        });
    };

    $scope.reconfigureServer = function (server) {
        $mdToast.show($mdToast.simple().textContent('Reconfiguring Server'));
        server.$reconfigure().then(function successCallback(response) {
            $mdToast.show($mdToast.simple().textContent('Server Reconfigured'));
            $scope.servers = Server.query();
        }, function errorCallback(response) {
            $mdToast.show($mdToast.simple().textContent('Problem Reconfiguring Server'));
            $scope.servers = Server.query();
        });
    };

    $scope.rebuildServer = function (ev, server) {
        var confirm = $mdDialog.confirm()
            .title('Are you sure you want to rebuild this server?')
            .textContent('This world will be reset to the starting state!')
            .targetEvent(ev)
            .ok('Yes, Please Rebuild!')
            .cancel('Cancel');

        $mdDialog.show(confirm).then(function () {
            $mdToast.show($mdToast.simple().textContent('Rebuilding Server'));
            server.$rebuild().then(function successCallback(response) {
                $mdToast.show($mdToast.simple().textContent('Server Rebuilt'));
                $scope.servers = Server.query();
            }, function errorCallback(response) {
                $mdToast.show($mdToast.simple().textContent('Problem Rebuilding Server'));
                $scope.servers = Server.query();
            });
        }, function () {
            // cancel
        });
    };

    $scope.deleteServer = function (ev, server) {
        var confirm = $mdDialog.confirm()
            .title('Are you sure you want to delete this server?')
            .textContent('This world will be lost forever!')
            .targetEvent(ev)
            .ok('Yes, Please Delete!')
            .cancel('Cancel');

        $mdDialog.show(confirm).then(function () {
            $mdToast.show($mdToast.simple().textContent('Deleting Server'));
            server.$delete().then(function successCallback(response) {
                $mdToast.show($mdToast.simple().textContent('Server Deleted'));
                $scope.servers = Server.query();
            }, function errorCallback(response) {
                $mdToast.show($mdToast.simple().textContent('Problem Deleting Server'));
                $scope.servers = Server.query();
            });
        }, function () {
            // cancel
        });
    };

    $scope.showCreateServer = function (ev) {
        $mdDialog.show({
            controller: DialogController,
            scope: $scope,
            templateUrl: 'static/app/views/definition-dialog.html',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: false,
            fullscreen: false
        });
    };

    function DialogController($scope, $mdDialog, Server, Map) {
        $scope.serverDefinition = new Server();
        $scope.maps = Map.query();

        $scope.save = function () {
            $mdDialog.hide();
            $mdToast.show($mdToast.simple().textContent('Creating Server'));
            $scope.serverDefinition.$save().then(function () {
                $scope.servers = Server.query();
                $mdToast.show($mdToast.simple().textContent('Server Created'));
            });
        };

        $scope.cancel = function () {
            $mdDialog.cancel();
        };
    }
});