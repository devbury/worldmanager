var app = angular.module('WorldManager', [
    'ngRoute',
    'ngMaterial',
    'ngResource'
]);

app.config(function ($routeProvider) {
    $routeProvider.when('/', {
        redirectTo: '/servers'
    });
    $routeProvider.when('/servers', {
        templateUrl: 'app/views/servers.html',
        controller: 'ServerController'
    });
    $routeProvider.when('/create', {
        templateUrl: 'app/views/main.html',
        controller: 'CreateController'
    });
    $routeProvider.when('/definitions', {
        templateUrl: 'app/views/definitions.html',
        controller: 'DefinitionController'
    });
    $routeProvider.otherwise({
        redirectTo: '/404'
    });
});

app.factory('Server', function ($resource) {
    return $resource('/api/server/:containerId',
        {
            containerId: '@containerId'
        },
        {
            'stop': {
                method: 'PUT',
                url: '/api/server/stop'
            },
            'start': {
                method: 'PUT',
                url: '/api/server/start'
            },
            'reconfigure': {
                method: 'PUT',
                url: '/api/server/reconfigure'
            }
        }
    );
});

app.factory('ServerDefinition', function ($resource) {
    return $resource('/api/definition/:name',
        {
            name: '@name'
        },
        {
            'save': {
                method: 'POST',
                url: '/api/definition'
            }
        }
    );
});