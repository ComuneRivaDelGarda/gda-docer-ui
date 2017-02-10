var gdadocerapp = angular.module('GDADocerApp', ['ngResource', 'ui.bootstrap', 'ngFileUpload'])
/**
 * Service per risorse REST DOCER
 */
.service('DocerService', ['$resource', function($resource) {
	var docerResource = $resource('./api/docer/documents/:id', {
		id : '@id'
	}, {
		// Let's make the `query()` method cancellable
		query : {
			method : 'get',
			isArray : true
		},
		'versions' : {
			url: './api/docer/documents/:id/versions',
			method : 'get',
			isArray : true
		}
		/*
		,
		'download' : {
			url: './api/docer/documents/:id/download',
			method : 'get',
			responseType: 'arraybuffer'
		}
		*/
	});
	return docerResource;
}])
/**
 * Controller per POPUP UPLOAD
 */
.controller('UploadController', ['$log', '$scope', '$uibModalInstance', 'Upload', function($log, $scope, $uibModalInstance, Upload) {
	var $ctrl = this;
//	$ctrl.items = items;
//	$ctrl.selected = {
//		item : $ctrl.items[0]
//	};
	
	$ctrl.ok = function() {
		$scope.submit();
		// $uibModalInstance.close();
	};

	$ctrl.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};

	// upload later on form submit or something similar
    $scope.submit = function() {
		if ($scope.form.file.$valid && $scope.file) {
			$scope.upload($scope.file);
		}
	};
	
	$scope.uploading = false;
	$scope.progressPercentage = 0;
	// upload on file select or drop
    $scope.upload = function (file) {
        Upload.upload({
            url : './api/docer/documents/upload',
			data : {
				titolo : $scope.titolo,
				file : file,
			}
        }).then(function (resp) {
        	// $scope.uploading = false;
        	$log.debug('Success ' + resp.config.data.file.name + 'uploaded. Response: ' + resp.data);
            $uibModalInstance.close();
        }, function (resp) {
        	$log.error('Error status: ' + resp.status);
        }, function (evt) {
        	$scope.uploading = true;
            $scope.progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
            $log.debug('progress: ' + $scope.progressPercentage + '% ' + evt.config.data.file.name);
        });
    };
    
}])
/**
 * Controller della pagina MAIN
 */
.controller('MainController', ['$log', '$scope', 'DocerService', '$uibModal', function($log, $scope, docerService, $uibModal) {
	// var $ctrl = this;

	$scope.docs = [];
	
	$scope.loadData = function() {
		$log.debug('loading data');
		$scope.docs = docerService.query();
	};
	$scope.loadData();

	$scope.versions = [];
	$scope.selectedDoc = null;
	$scope.resetSelection = function(doc) {
		$scope.versions = [];
		$scope.selectedDoc = null;
	};
	$scope.resetSelection();
	
	$scope.unselectCurrentDoc = function() {
		if ($scope.selectedDoc) {
			$scope.selectedDoc.selected = false; // equivale a doc.selected = false
			$scope.resetSelection();			
		}
	};
	$scope.selectDoc = function(doc) {
		$log.debug("selectDoc");
		// se stesso file lo deseleziono
		// TODO: se seleziono da file a cartella, da file a file, stesso file
		if ($scope.selectedDoc && $scope.selectedDoc.id === doc.id)
			return;
		else
			$scope.unselectCurrentDoc();
		
//			// $scope.selectedDoc.selected = false; // equivale a doc.selected = false
//			// $scope.resetSelection();
//		} else {
			// deseleziono vecchio
		

		if (doc) {
			// seleziono corrente
			doc.selected = true;
			$scope.selectedDoc = doc;
			if (!doc.directory) {
				$log.debug('loading version for ' + doc.nome);
				docerService.versions({
					id : doc.id
				}, function(versions) {
					$scope.versions = versions;
				});
			}
		}
		
	};
	$scope.browseDoc = function(doc) {
		$scope.unselectCurrentDoc();
		$log.debug("browseDoc");
	};
	$scope.clickRow = function(doc) {
		$log.debug("clickRow");
		if (doc.directory) {
			$scope.browseDoc(doc)
		}
		else {
			$scope.selectDoc(doc)
		}
	}
	$scope.doubleClickRow = function(doc) {
		$log.debug("doubleClickRow");
	};
	/*
	$scope.download = function(doc) {
		$log.debug('download for ' + doc.nome);
		docerService.download({
			id : doc.id
		}, function(response) {
			
			var data = new Blob([response], { type: doc.contentType + ';charset=utf-8' });
		    FileSaver.saveAs(data, doc.nome);
					    
//			var url = URL.createObjectURL(new Blob([ data ]));
//			var a = document.createElement('a');
//			a.href = url;
//			a.download = doc.nome;
//			a.target = '_blank';
//			a.click();
		});
	}
	*/
	
	$scope.openUploadModals = function() {
		$log.debug('openUploadModals');
//		$uibModal.open({
//			animation : true,
//			ariaLabelledBy : 'modal-title-bottom',
//			ariaDescribedBy : 'modal-body-bottom',
//			templateUrl : 'partials/uploadDocument.html',
//			// size : 'sm',
//			controller : 'UploadController',
//			controllerAs : '$ctrl',
//		});
		    
		var modalInstance = $uibModal.open({
			animation : true,
			ariaLabelledBy : 'modal-title',
			ariaDescribedBy : 'modal-body',
			templateUrl : 'partials/uploadDocument.html',
			controller : 'UploadController',
			controllerAs : '$ctrl',
			// size : 'lg',
			// appendTo : parentElem,
	//		resolve : {
	//			items : function() {
	//				return $ctrl.items;
	//			}
	//		}
		});
	
		modalInstance.result.then(function () {
			$log.info('Modal OK at: ' + new Date());
			$scope.loadData();
	    }, function () {
    		$log.info('Modal dismissed at: ' + new Date());
	    });
	};
	
}]);