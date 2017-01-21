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
    $routeProvider.otherwise({
        redirectTo: '/404'
    });
});

app.factory('Server', function ($resource) {
    return $resource('/api/server/:name',
        {
            name: '@name'
        },
        {
            'save': {
                method: 'POST',
                url: '/api/server'
            },
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
            },
            'rebuild': {
                method: 'PUT',
                url: '/api/server/rebuild'
            }
        }
    );
});