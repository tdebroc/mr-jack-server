import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

/**
 * Module class for application http interceptor.
 * @implements HttpInterceptor
 * @class AppHttpInterceptorService.
 */
@Injectable()
export class AppHttpInterceptorService implements HttpInterceptor {

  constructor() {
  }

  public intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next
      .handle(req)
      .pipe(catchError(err => {
        if (err instanceof HttpErrorResponse) {
          AppHttpInterceptorService.onError(err);
        }
        return throwError(err);
      }));
  }

  /**
   * Handle http errors.
   * @param response - ErrorResponse.
   */
  private static onError(response: HttpErrorResponse): void {
    const clientErrorMessage = AppHttpInterceptorService.handleClientSideError(response.status);
    if (clientErrorMessage) {
      // show client side error
      return;
    }
    alert("Error: " + response.error)
    const serverErrorMessage = AppHttpInterceptorService.handleServerError(response.error);
    if (serverErrorMessage) {
      // show server error
    }
  }

  private static handleClientSideError(status: number): string | undefined {
    switch (status) {
      case 0:
        return 'NO INTERNET CONNECTION';
      case 404:
        return 'REQUEST FAILURE';
      default:
        return;
    }
  }

  private static handleServerError(errorResponse: any): string {
    console.log(errorResponse);
    // handle server error
    return '';
  }
}
