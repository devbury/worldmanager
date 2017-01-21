app.controller('DefinitionController', function ($scope, $http, $mdToast, $mdDialog, ServerDefinition, Server) {
    $scope.serverDefinitions = ServerDefinition.query();
    $scope.servers = Server.query();

    $scope.serverDoesNotExist = function (name) {
        return !$scope.servers.map(function(e) { return e.name}).includes(name);
    };

    $scope.createServer = function (definitionName) {
        $mdToast.show($mdToast.simple().textContent('Creating Server From Definition'));
        Server.save({containerId: definitionName});
        $scope.servers = Server.query();
    };

    $scope.deleteDefinition = function (serverDefinition) {
        serverDefinition.$delete();
        $scope.serverDefinitions = ServerDefinition.query();
    };

    $scope.showCreateDefinition = function (ev) {
        $mdDialog.show({
            controller: DialogController,
            templateUrl: 'static/app/views/dialog.html',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: false,
            fullscreen: false
        }).then(function (answer) {
            $mdToast.show($mdToast.simple().textContent('Server Definition Created'));
            $scope.serverDefinitions = ServerDefinition.query();
        });
    };

    function DialogController($scope, $mdDialog) {
        $scope.serverDefinition = new ServerDefinition();

        $scope.save = function () {
            $scope.serverDefinition.$save();
            $mdDialog.hide();
        };

        $scope.cancel = function () {
            $mdDialog.cancel();
        };
    }
});