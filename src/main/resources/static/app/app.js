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
    $routeProvider.when('/maps', {
        templateUrl: 'app/views/maps.html',
        controller: 'MapController'
    });
    $routeProvider.otherwise({
        redirectTo: '/404'
    });
});

app.directive('ngFiles', function ($parse) {
    function fn_link(scope, element, attrs) {
        var onChange = $parse(attrs.ngFiles);
        element.on('change', function (event) {
            onChange(scope, {$files: event.target.files});
        });
    }

    return {
        link: fn_link
    }
});

app.factory('Map', function ($resource) {
    return $resource('/api/map/:name',
        {
            name: '@name'
        }
    );
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

app.run(function ($http, $rootScope) {
    $http.get('https://launchermeta.mojang.com/mc/game/version_manifest.json').then(function (r) {
        $rootScope.versions = r.data.versions.map(function (v) {
            return v.id
        });
    });
});