/**
 * 
 */
var gdadocerapp = angular.module('GDADocerApp', ['ngResource', 'ui.bootstrap', 'ngFileUpload', 'angular-loading-bar', 'toaster', 'ngAnimate'])
/**
 * Service SESSIONE passaggio dati per MODAL
 */
.service('SessionService', ['$log', function($log) {
	$log.debug('SessionService');
	var SessionService = this;
	/* folder corrente */
	SessionService.folderId = 0;
	/* dati per gestire revisione di un file */
	SessionService.versione = false;
	SessionService.document = null;
	
	return SessionService;
}])
/**
 * Service per risorse REST DOCER
 */
.service('DocerService', ['$resource', '$log', function($resource, $log) {
	$log.debug('DocerService');
	var docerResource = $resource('./api/docer/documents/:id', {
		id : '@id'
	}, {
		query : {
			method : 'GET',
			isArray : true
		},
		'profiles' : {
			url: './api/docer/documents/:id/profiles',
			method : 'GET',
			isArray : true
		},
		'childs' : {
			url: './api/docer/documents/:id/childs',
			method : 'GET',
			isArray : true
		},		
		'profile' : {
			url: './api/docer/documents/:id/profile',
			method : 'GET',
			isArray : false
		},
		'versions' : {
			url: './api/docer/documents/:id/versions',
			method : 'GET',
			isArray : true
		},
		'delete' : {
			url: './api/docer/documents/:id/delete',
			method : 'DELETE',
			isArray : false
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
 * Controller della pagina MAIN
 */
.controller('MainController', ['$log', '$scope', '$location', 'DocerService', '$uibModal', 'SessionService', '$timeout', 'toaster', '$filter', function($log, $scope, $location, docerService, $uibModal, SessionService, $timeout, toaster, $filter) {
	// var $ctrl = this;
	$scope.folderIdParent = null;
	$scope.folderId = null;
	if ($location.search().id) {
		$scope.folderId = $location.search().id;
	}
	
	$scope.docs = [];
	$scope.orderByField='DOCNAME';
	$scope.sortAsc=true;
	
	$scope.isLoadingData = false;
	$scope.isLoadingVersioni = false;
	
	$scope.loadData = function() {
		$log.debug('loading data');
		$scope.isLoadingData = true;
		$scope.docs = [];
		/* versione 1 */
		/*
		docerService.query({
			id : folderId
		}, function (folderData) {
			if (folderData) {
				$log.debug('data loaded');
				// $scope.docs = folderData;
				angular.forEach(folderData, function(childDocumenId) {
					docerService.profile({
						id : childDocumenId
					}, function (documentProfile) {
						if (documentProfile) {
							$scope.docs.push(documentProfile);
						}
					});
				});
			} else {
				$log.debug('no data loaded');
				// TODO: messaggio noda
			}
		});
		*/
		/* versione 2 */
		docerService.childs({
			id : $scope.folderId
		}, function (childs) {
			$scope.isLoadingData = false;
			$log.debug('childs loaded');
			if (childs) {
				$scope.docs = childs;
				// $scope.docs = $filter('orderBy')(childs, $scope.orderByField, $scope.sortAsc);
			}
		}, function (childsErrorResponse) {
			$scope.isLoadingData = false;
			$log.error(childsErrorResponse);
			toaster.pop({
                type: 'error',
                title: "Errore durante il caricamento dei dati.",
                body: childsErrorResponse,
                showCloseButton: true
            });				
		});
	};

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
		if ($scope.selectedDoc && $scope.selectedDoc.DOCNUM === doc.DOCNUM)
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
			if (doc.DOCNUM) {
				$log.debug('loading version for ' + doc.DOCNUM);
				$scope.isLoadingVersions = true;
				docerService.versions({
					id : doc.DOCNUM
				}, function(versions) {
					$scope.isLoadingVersions = false;
					$scope.versions = versions;
				}, function(versionsErrorResponse) {
					$log.error(versionsErrorResponse);
					toaster.pop({
		                type: 'error',
		                title: "Errore durante il caricamento delle versioni.",
		                body: versionsErrorResponse,
		                showCloseButton: true
		            });				
					$scope.isLoadingVersions = false;				
				});
			}
		}
		
	};
	$scope.browseDoc = function(doc) {
		$scope.unselectCurrentDoc();
		$log.debug("browseDoc");
		$scope.folderIdParent = $scope.folderId;
		$scope.folderId = doc.FOLDER_ID;
		$scope.loadData();
	};
	$scope.clickRow = function(doc) {
		$log.debug("clickRow");
		//if (doc.FOLDER_ID) {
			//$scope.browseDoc(doc)
		//}
		if (doc.DOCNUM) {
			$scope.selectDoc(doc)
		}
	}
	$scope.doubleClickRow = function(doc) {
		$log.debug("doubleClickRow");
		if (doc.FOLDER_ID) {
			$scope.browseDoc(doc)
		}
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
	$scope.levelUp = function() {
		$scope.unselectCurrentDoc();
		$log.debug("levelUp");
		if ($scope.folderIdParent) {
			$scope.folderId = $scope.folderIdParent;
			$scope.loadData();
		}
	};
		
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
		
		$log.debug('SessionService.folderId='+$scope.folderId);
		SessionService.folderId = $scope.folderId;
		
		var modalInstance = $uibModal.open({
			animation : true,
			ariaLabelledBy : 'modal-title',
			ariaDescribedBy : 'modal-body',
			templateUrl : 'partials/uploadDocument.html',
			controller : 'UploadController',
			controllerAs : '$ctrl'
			// size : 'lg',
			// appendTo : parentElem,
//			resolve : {
//				folderId : function() {
//					return $ctrl.items;
//				}
//			}
		});
	
		modalInstance.result.then(function () {
			$log.info('Modal OK at: ' + new Date());
			$scope.loadData();
	    }, function () {
    		$log.info('Modal dismissed at: ' + new Date());
	    });	
	};
	
	if ($scope.folderId) {
		$scope.loadData();
		// folderId = 885160; 885161
	} else {
		$log.debug('id='+$scope.folderId);
		$scope.folderId = "";
		$timeout(function() {
			toaster.pop('warning', "Nessuna cartella specificata", "");
		}, 250);
		
	}
	
	$scope.pop = function(){
        toaster.pop('warning', "title", "text");
    };
    /*
     * pulsante Carica Documento
     */
    $scope.addFile = function() {
    	SessionService.versione = false;
    	SessionService.document = null;
        $scope.openUploadModals();
    };
    /*
     * pulsante Carica una nuova versione
     */
    $scope.versione = function() {
    	SessionService.versione = true;
    	SessionService.document = $scope.selectedDoc;
        $scope.openUploadModals();
    };
    /*
     * pulsante Elimina Documento
     */
	$scope.deleteFile = function() {
		$log.debug("deleteFile");
		var doc = $scope.selectedDoc;
		if (doc) {
			if (confirm('Sicuro di voler eliminare il file ' + doc.DOCNAME + ' ?')) {
				docerService.delete({
					id : doc.DOCNUM
				}, function (deleteResponse) {
					$log.debug("deleteResponse=" + deleteResponse);
					if (deleteResponse) {
						$log.debug('doc ' + doc.DOCNUM + ' deleted');
						toaster.pop({
		                    type: 'success',
		                    title: 'File ' + doc.DOCNAME + ' eliminato.',
		                    body: '',
		                    showCloseButton: true
		                });
						$scope.loadData();
					} else {
						toaster.pop({
		                    type: 'warning',
		                    title: 'File ' + doc.DOCNAME + ' eliminato.',
		                    body: '',
		                    showCloseButton: true
		                });
					}
				}, function (deleteError) {
					toaster.pop({
	                    type: 'error',
	                    title: "Errore durante l'eliminazione del file.",
	                    body: error,
	                    showCloseButton: true
	                });
				});
				/*
				var resource = docerService.get({
					id : doc.DOCNUM
				}, function() {
					$log.debug(angular.toJson(doc));
					// user.abc = true;
					// user.$save();
					resource.$delete();
				});
				*/
			}
		}
	};
}])
/**
 * Controller per POPUP UPLOAD
 */
.controller('UploadController', ['$log', '$scope', '$uibModalInstance', 'Upload', 'SessionService', 'toaster', function($log, $scope, $uibModalInstance, Upload, SessionService, toaster) {
	var $ctrl = this;
	// $ctrl.folderId = SessionService.folderId;
	// $scope.folderId = SessionService.folderId;
//	$ctrl.selected = {
//		item : $ctrl.items[0]
//	};
	// $ctrl.folderId = $ctrl.folderId;
	$ctrl.titoloPopup = "Documento";
	$ctrl.revisione = false;
	if (SessionService.versione) {
		$ctrl.titoloPopup = "Versione";
		$ctrl.revisione = true;
	}
	
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
    	var uploadUrl = '';
    	var uploadData = {};
    	if (SessionService.versione) {
    		$log.debug("revisione documento " + SessionService.document.DOCNUM);
    		uploadUrl = './api/docer/documents/'+SessionService.document.DOCNUM+'/versione';
    		uploadData = {
				abstract : $scope.abstract,
				file : file
			};
    	} else {
    		$log.debug("upload su folder " + SessionService.folderId);
    		uploadUrl = './api/docer/documents/'+SessionService.folderId+'/upload';
    		uploadData = {
				abstract : $scope.abstract,
    			tipoComponente : $scope.tipoComponente,
    			file : file
    		}
    	}
    	// chiamata a metodo upload con parametri url e data impostati in precedenza
		Upload.upload({
            url : uploadUrl,
			data : uploadData
        }).then(function (resp) {
        	// $scope.uploading = false;
        	$log.debug('Success ' + resp.config.data.file.name + 'uploaded. Response: ' + resp.data);
        	toaster.pop({
                type: 'success',
                title: 'File caricato',
                body: '',
                showCloseButton: true
            });
            $uibModalInstance.close();
        }, function (resp) {
        	// $log.error('Error status: ' + resp.status);
        	$log.error(resp);
        	toaster.pop({
                type: 'error',
                title: 'Errore caricamento file',
                body: 'Errore: ' + resp.status + ' - ' + resp.data,
                showCloseButton: true
            });
        }, function (evt) {
        	$scope.uploading = true;
            $scope.progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
            $log.debug('progress: ' + $scope.progressPercentage + '% ' + evt.config.data.file.name);
        });    		
    };
}]);