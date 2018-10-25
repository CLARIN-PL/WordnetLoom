import { Injectable } from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Rx';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/finally';
import 'rxjs/add/operator/catch';
// import {SlimLoadingBarService} from 'ng2-slim-loading-bar';

@Injectable()
export class HttpService {
  // apiBase = '/yiddish/api/';
  apiBase = 'http://156.17.135.29:8080/yiddish/api/';

  constructor(private http: Http) {}

  private handleError() {
    // todo
  }

  private get(uri, base?: string): Observable<any> {
    // this.slimLoadingBarService.start();

    // const headers = new Headers(options);
    // const opt = new RequestOptions({ headers: headers});

    if (!base) {
      base = this.apiBase;
    }

    return this.http.get(base + uri)
      .map( res => res.json())
      .finally(() => {});
  }


  getLexicalUnitDetails(id) {
    return this.get('sense/' + id);
  }

  getSearchOptions(form: String, page = 0, perPage= 50) {
    let searchStr = 'sense?';
    for (const key in form) {
      if (form[key] !== '' && form[key] !== undefined) {
        searchStr += key + '=' + form[key] + '&';
      }
    }
    return this.get(searchStr + 'per_page=' + perPage + '&page=' + page);
  }

  getGlobalOptions(searchedKey) {
    return this.get(searchedKey);
  }

  getSenseRelations(senseId) {
    const incoming = this.get('sense/' + senseId + '/relations/incoming').map(res => res);
    const outgoing = this.get('sense/' + senseId + '/relations/outgoing').map(res => res);

    return Observable.forkJoin([incoming, outgoing]);
  }

  getSearchAutocomplete(term) {
    // todo- get from search
    return Observable.of(new Object()).mapTo([term, 'abc', 'cde']);
  }
  getLang(lang) {
    const langPath = `assets/i18n/${lang || 'en'}.json`;

    return this.http.get(langPath).map( res => res.json())
      .finally(() => {});
    // return this.get(langPath, '/');
  }
}