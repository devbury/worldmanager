app.controller('ServerController', function ($scope, Server, $mdToast, $mdDialog) {
    $scope.servers = Server.query();

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
});