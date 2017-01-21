app.controller('ServerController', function ($scope, Server, $mdToast, $mdDialog) {
    $scope.servers = Server.query();

    $scope.serverDoesNotExist = function (name) {
        return !$scope.servers.map(function(e) { return e.name}).includes(name);
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
            templateUrl: 'static/app/views/dialog.html',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: false,
            fullscreen: false
        }).then(function (answer) {
            $mdToast.show($mdToast.simple().textContent('Server Definition Created'));
        });
    };

    function DialogController($scope, $mdDialog) {
        $scope.serverDefinition = new Server();

        $scope.save = function () {
            $scope.serverDefinition.$save();
            $scope.servers = Servers.query();
            $mdDialog.hide();
        };

        $scope.cancel = function () {
            $mdDialog.cancel();
        };
    }
});